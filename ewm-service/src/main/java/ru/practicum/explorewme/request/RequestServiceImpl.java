package ru.practicum.explorewme.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewme.event.EventRepository;
import ru.practicum.explorewme.event.model.Event;
import ru.practicum.explorewme.event.model.EventState;
import ru.practicum.explorewme.exception.EntityNotFoundException;
import ru.practicum.explorewme.exception.ValidationException;
import ru.practicum.explorewme.request.dto.RequestDto;
import ru.practicum.explorewme.request.model.Request;
import ru.practicum.explorewme.request.model.RequestStatus;
import ru.practicum.explorewme.user.UserRepository;
import ru.practicum.explorewme.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private RequestMapper requestMapper;

    @Override
    @Transactional
    public RequestDto create(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Событие с id=%d не существует", eventId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Пользователь с id=%d не существует", userId)));


        //нельзя добавить повторный запрос
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ValidationException("Нельзя добавить повторный запрос");
        }

        //инициатор события не может добавить запрос на участие в своём событии
        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Нельзя добавить запрос на участие в своем событии");
        }

        //нельзя участвовать в неопубликованном событии
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Нельзя участвовать в неопубликованном событии");
        }

        //если у события достигнут лимит запросов на участие
        if (getConfirmedRequestsCount(eventId) == event.getParticipantLimit()
                && event.getParticipantLimit() > 0) {
            throw new ValidationException("У события достигнут лимит запросов на участие");
        }

        Request request = new Request();

        request.setCreated(LocalDateTime.now());

        request.setRequester(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Пользователь с id=%d не существует", userId)))
        );

        request.setEvent(event);

        //если для события отключена пре-модерация запросов на участие, то переход в состояние подтвержденного
        if (event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public Integer getConfirmedRequestsCount(Long eventId) {
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

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Событие с id=%d не существует", eventId)));

        //проверяем что это событие пользователя userId и запрос для этого эвента
        if (!request.getEvent().getId().equals(eventId) || !request.getEvent().getInitiator().getId().equals(userId)) {
            throw new EntityNotFoundException(String.format(
                    "Запрос id=%d на участие в событии (eventId=%d, initiator_id=%d) не найден",
                    requestId, eventId, userId));
        }

        if (!event.getRequestModeration()) {
            throw new EntityNotFoundException(String.format("Событие с id=%d не требует модерации запросов", eventId));
        }

        if (event.getParticipantLimit() == requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new EntityNotFoundException(String.format("Событие с id=%d достигло максимума участников", eventId));
        }

        if (event.getParticipantLimit() == requestRepository.countByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED) + 1) {
            List<Request> requests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);

            for (Request req : requests) {
                reject(req.getId());
            }
        }

        return confirm(requestId);
    }

    @Override
    @Transactional
    public RequestDto rejectRequest(Long userId, Long eventId, Long requestId) {
        Request request = requestRepository.findByIdAndStatus(requestId, RequestStatus.PENDING).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                        "Запрос на участие в событии с id=%d с неподтвержденным статусом не найден", requestId)));

        //проверяем что это событие пользователя userId и запрос для этого эвента
        if (!request.getEvent().getId().equals(eventId) || !request.getEvent().getInitiator().getId().equals(userId)) {
            throw new EntityNotFoundException(String.format(
                    "Запрос id=%d на участие в событии (eventId=%d, initiator_id=%d) не найден",
                    requestId, eventId, userId));
        }

        return reject(requestId);
    }
}
