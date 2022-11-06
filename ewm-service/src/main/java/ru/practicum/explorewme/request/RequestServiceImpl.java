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
import ru.practicum.explorewme.request.requeststatus.RequestStatusService;
import ru.practicum.explorewme.user.UserMapper;
import ru.practicum.explorewme.user.UserService;
import ru.practicum.explorewme.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {
    private RequestRepository requestRepository;

    private UserService userService;

    private EventService eventService;

    private RequestStatusService requestStatusService;

    private EventMapper eventMapper;

    private UserMapper userMapper;

    private RequestMapper requestMapper;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserService userService,
                              @Lazy EventService eventService, RequestStatusService requestStatusService,
                              EventMapper eventMapper, UserMapper userMapper, RequestMapper requestMapper) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.eventService = eventService;
        this.requestStatusService = requestStatusService;
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
        this.requestMapper = requestMapper;
    }

    @Override
    @Transactional
    public RequestDto create(Long userId, Long eventId) throws EntityNotFoundException, ValidationException {
        EventFullDto eventFullDto = eventService.findById(eventId);

        UserDto userDto = userService.findById(userId);


        //нельзя добавить повторный запрос
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ValidationException("Нельзя добавить повторный запрос");
        }

        //инициатор события не может добавить запрос на участие в своём событии
        if (eventFullDto.getInitiator().getId() == userId) {
            throw new ValidationException("Нельзя добавить запрос на участие в своем событии");
        }

        //нельзя участвовать в неопубликованном событии
        if (!eventFullDto.getState().equals("PUBLISHED")) {
            throw new ValidationException("Нельзя участвовать в неопубликованном событии");
        }

        //если у события достигнут лимит запросов на участие
        if (requestRepository.findConfirmedRequestsCount(eventId) == eventFullDto.getParticipantLimit()
                && eventFullDto.getParticipantLimit() > 0) {
            throw new ValidationException("У события достигнут лимит запросов на участие");
        }

        Request request =  new Request();

        request.setCreated(LocalDateTime.now());

        request.setRequester(userMapper.toUser(userService.findById(userId)));

        request.setEvent(eventMapper.toEvent(eventFullDto, userDto));

        //если для события отключена пре-модерация запросов на участие, то переход в состояние подтвержденного
        if (eventFullDto.getRequestModeration()) {
            request.setStatus(requestStatusService.findById(1L));
        } else {
            request.setStatus(requestStatusService.findById(2L));
        }

        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public Long getConfirmedRequestsCount(Long eventId) {
        return requestRepository.findConfirmedRequestsCount(eventId);
    }

    @Override
    public List<RequestDto> findAllByRequester(Long userId) {
        return requestMapper.toRequestDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    @Transactional
    public RequestDto cancel(Long userId, Long eventId) throws EntityNotFoundException {
        Request request = requestRepository.findOwnForCancel(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Запрос на участие в событии с id=%d не найден", eventId)));

        request.setStatus(requestStatusService.findById(3L)); //CANCELED

        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestDto reject(Long requestId) throws EntityNotFoundException {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Запрос на участие в id=%d не найден", requestId)));

        request.setStatus(requestStatusService.findById(4L)); //REJECTED

        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> findAllByEvent(Long eventId) {
        return requestMapper.toRequestDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    @Transactional
    public RequestDto confirm(Long requestId) throws EntityNotFoundException {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Запрос на участие в событии с id=%d не найден", requestId)));

        request.setStatus(requestStatusService.findById(2L)); //CONFIRMED

        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestDto confirmRequest(Long userId, Long eventId, Long requestId) throws EntityNotFoundException {
        Request request = requestRepository.findByIdAndStatusId(requestId, 1L).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                        "Запрос на участие в событии с id=%d с неподтвержденным статусом не найден", requestId)));

        EventFullDto eventFullDto = eventService.findByIdAndInitiator(eventId, userId);

        if (!eventFullDto.getRequestModeration()) {
            throw new EntityNotFoundException(String.format("Событие с id=%d не требует модерации запросов", eventId));
        }

        if (eventFullDto.getParticipantLimit() == eventFullDto.getConfirmedRequests()) {
            throw new EntityNotFoundException(String.format("Событие с id=%d достигло максимума участников", eventId));
        }

        if (eventFullDto.getParticipantLimit() == eventFullDto.getConfirmedRequests() + 1) {
            List<Request> requests = requestRepository.findAllByEventIdAndStatusId(eventId, 1L);

            for (Request req : requests) {
                reject(req.getId());
            }
        }

        return confirm(requestId);
    }

    @Override
    public RequestDto rejectRequest(Long userId, Long eventId, Long requestId) throws EntityNotFoundException {
        Request request = requestRepository.findByIdAndStatusId(requestId, 1L).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                        "Запрос на участие в событии с id=%d с неподтвержденным статусом не найден", requestId)));

        return reject(requestId);
    }
}
