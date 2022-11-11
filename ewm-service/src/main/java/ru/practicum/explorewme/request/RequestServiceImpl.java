package ru.practicum.explorewme.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewme.event.EventMapper;
import ru.practicum.explorewme.event.EventService;
import ru.practicum.explorewme.event.dto.EventFullDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;
import ru.practicum.explorewme.exception.ValidationException;
import ru.practicum.explorewme.request.dto.RequestDto;
import ru.practicum.explorewme.request.model.Request;
import ru.practicum.explorewme.request.model.RequestStatus;
import ru.practicum.explorewme.user.UserMapper;
import ru.practicum.explorewme.user.UserService;
import ru.practicum.explorewme.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private RequestRepository requestRepository;
    private UserService userService;
    private EventService eventService;
    private EventMapper eventMapper;
    private UserMapper userMapper;
    private RequestMapper requestMapper;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserService userService,
                              @Lazy EventService eventService, EventMapper eventMapper, UserMapper userMapper,
                              RequestMapper requestMapper) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.eventService = eventService;
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
        this.requestMapper = requestMapper;
    }

    @Override
    @Transactional
    public RequestDto create(Long userId, Long eventId) {
        EventFullDto eventFullDto = eventService.findById(eventId);

        UserDto userDto = userService.findById(userId);


        //нельзя добавить повторный запрос
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ValidationException("Нельзя добавить повторный запрос");
        }

        //инициатор события не может добавить запрос на участие в своём событии
        if (eventFullDto.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Нельзя добавить запрос на участие в своем событии");
        }

        //нельзя участвовать в неопубликованном событии
        if (!eventFullDto.getState().equals("PUBLISHED")) {
            throw new ValidationException("Нельзя участвовать в неопубликованном событии");
        }

        //если у события достигнут лимит запросов на участие
        if (getConfirmedRequestsCount(eventId) == eventFullDto.getParticipantLimit()
                && eventFullDto.getParticipantLimit() > 0) {
            throw new ValidationException("У события достигнут лимит запросов на участие");
        }

        Request request = new Request();

        request.setCreated(LocalDateTime.now());

        request.setRequester(userMapper.toUser(userService.findById(userId)));

        request.setEvent(eventMapper.toEvent(eventFullDto, userDto));

        //если для события отключена пре-модерация запросов на участие, то переход в состояние подтвержденного
        if (eventFullDto.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public Long getConfirmedRequestsCount(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    @Override
    public List<RequestDto> findAllByRequester(Long userId) {
        return requestMapper.toRequestDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    @Transactional
    public RequestDto cancel(Long userId, Long eventId) {
        Request request = requestRepository.findByIdAndRequesterIdAndStatusIn(
                        eventId, userId, List.of(RequestStatus.PENDING, RequestStatus.CONFIRMED))
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Запрос на участие в событии с id=%d не найден", eventId)));

        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestDto reject(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Запрос на участие в id=%d не найден", requestId)));

        request.setStatus(RequestStatus.REJECTED);

        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> findAllByEvent(Long eventId) {
        return requestMapper.toRequestDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    public List<RequestDto> findAllByEventAndOwner(Long eventId, Long userId) {
        return requestMapper.toRequestDto(requestRepository.findAllByEventAndOwner(userId, eventId));
    }

    @Override
    @Transactional
    public RequestDto confirm(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Запрос на участие в событии с id=%d не найден", requestId)));

        request.setStatus(RequestStatus.CONFIRMED);

        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestDto confirmRequest(Long userId, Long eventId, Long requestId) {
        Request request = requestRepository.findByIdAndStatus(requestId, RequestStatus.PENDING).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                        "Запрос на участие в событии с id=%d с неподтвержденным статусом не найден", requestId)));

        EventFullDto eventFullDto = eventService.findByIdAndInitiator(eventId, userId);

        //проверяем что это событие пользователя userId и запрос для этого эвента
        if (request.getEvent().getId().equals(eventId) && request.getEvent().getInitiator().getId().equals(userId)) {

            if (!eventFullDto.getRequestModeration()) {
                throw new EntityNotFoundException(String.format("Событие с id=%d не требует модерации запросов", eventId));
            }

            if (eventFullDto.getParticipantLimit() == eventFullDto.getConfirmedRequests()) {
                throw new EntityNotFoundException(String.format("Событие с id=%d достигло максимума участников", eventId));
            }

            if (eventFullDto.getParticipantLimit() == eventFullDto.getConfirmedRequests() + 1) {
                List<Request> requests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);

                for (Request req : requests) {
                    reject(req.getId());
                }
            }
            return confirm(requestId);

        } else {
            throw new EntityNotFoundException(String.format(
                    "Запрос id=%d на участие в событии (eventId=%d, initiator_id=%d) не найден",
                    requestId, eventId, userId));
        }
    }

    @Override
    @Transactional
    public RequestDto rejectRequest(Long userId, Long eventId, Long requestId) {
        Request request = requestRepository.findByIdAndStatus(requestId, RequestStatus.PENDING).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                        "Запрос на участие в событии с id=%d с неподтвержденным статусом не найден", requestId)));

        //проверяем что это событие пользователя userId и запрос для этого эвента
        if (request.getEvent().getId().equals(eventId) && request.getEvent().getInitiator().getId().equals(userId)) {
            return reject(requestId);
        } else {
            throw new EntityNotFoundException(String.format(
                    "Запрос id=%d на участие в событии (eventId=%d, initiator_id=%d) не найден",
                    requestId, eventId, userId));
        }
    }
}
