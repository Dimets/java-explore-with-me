package ru.practicum.explorewme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.event.dto.EventFullDto;
import ru.practicum.explorewme.event.dto.EventShortDto;
import ru.practicum.explorewme.event.dto.NewEventDto;
import ru.practicum.explorewme.event.dto.UpdateEventRequest;
import ru.practicum.explorewme.exception.EntityNotFoundException;
import ru.practicum.explorewme.request.RequestService;
import ru.practicum.explorewme.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    //Добавление нового события
    @PostMapping(path = "/{userId}/events")
    public EventFullDto createEvent(@PathVariable("userId") Long userId,
                                    @RequestBody @Valid NewEventDto newEventDto)
            throws EntityNotFoundException {
        log.info("POST /users/{}/events", userId);
        log.debug("POST /users/{}/events newEventDto={}", userId, newEventDto);
        return eventService.create(newEventDto, userId);
    }

    //Получение событий, добавленных текущим пользователем
    @GetMapping(path = "/{userId}/events")
    public List<EventShortDto> findAllByInitiator(
            @PathVariable("userId") Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET /users/{}/events from={}, size={}", userId,from, size);

        return eventService.findAllByInitiator(userId, from, size);
    }

    //Изменение события, добавленного текущим пользователем
    @PatchMapping(path = "/{userId}/events")
    public EventFullDto updateEvent(@PathVariable("userId") Long userId,
                                    @RequestBody @Valid UpdateEventRequest updateEventRequest)
            throws EntityNotFoundException {
        log.info("PATCH /users/{}/events", userId);
        log.info("PATCH /users/{}/events updateEventRequest={}", userId, updateEventRequest);

        return eventService.update(updateEventRequest, userId);
    }

    //Получение полной информации о событии, доабавленном текущим пользователем
    @GetMapping(path = "/{userId}/events/{eventId}")
    public EventFullDto findByIdAndInitiator(@PathVariable("userId") Long userId,
                                             @PathVariable("eventId") Long eventId) throws EntityNotFoundException {
        log.info("GET /users/{}/events/{}", userId, eventId);

        return eventService.findByIdAndInitiator(eventId, userId);
    }

    //Отмена события, добавленного текущим пользовтаелем
    @PatchMapping(path = "/{userId}/events/{eventId}")
    public EventFullDto cancelEvent(@PathVariable("userId") Long userId,
                                             @PathVariable("eventId") Long eventId) throws EntityNotFoundException {
        log.info("PATCH /users/{}/events/{}", userId, eventId);

        return eventService.cancelEvent(eventId, userId);
    }

    //Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping(path = "/{userId}/events/{eventId}/requests")
    public List<RequestDto> findAllRequestsForEvent(@PathVariable("userId") Long userId,
                                                    @PathVariable("eventId") Long eventId) throws EntityNotFoundException {
        log.info("/GET /{}/events/{}/requests", userId, eventId);

        eventService.findByIdAndInitiator(eventId, userId);

        return requestService.findAllByEvent(eventId);
    }

    //Подтверждение чужой заявки на участие в событии текущего пользователя
    @PatchMapping(path = "/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmRequest(@PathVariable("userId") Long userId, @PathVariable("eventId") long eventId,
                                     @PathVariable("reqId") Long reqId) throws EntityNotFoundException {
        log.info("/PATCH /{}/events/{}/requests/{}/confirm", userId, eventId, reqId);

        return requestService.confirmRequest(userId, eventId, reqId);
    }

    //Отклонение чужой заявки на участие в событии текущего пользователя
    @PatchMapping(path = "/{userId}/events/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectRequest(@PathVariable("userId") Long userId, @PathVariable("eventId") long eventId,
                                    @PathVariable("reqId") Long reqId) throws EntityNotFoundException {
        log.info("/PATCH /{}/events/{}/requests/{}/reject", userId, eventId, reqId);

        return requestService.rejectRequest(userId, eventId, reqId);
    }
}
