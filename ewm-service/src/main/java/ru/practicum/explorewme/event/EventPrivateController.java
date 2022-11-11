package ru.practicum.explorewme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.event.dto.EventFullDto;
import ru.practicum.explorewme.event.dto.EventShortDto;
import ru.practicum.explorewme.event.dto.NewEventDto;
import ru.practicum.explorewme.event.dto.UpdateEventRequest;
import ru.practicum.explorewme.request.RequestService;
import ru.practicum.explorewme.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    //Добавление нового события
    @PostMapping
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @RequestBody @Valid NewEventDto newEventDto) {
        log.info("POST /users/{}/events", userId);
        log.debug("POST /users/{}/events newEventDto={}", userId, newEventDto);
        return eventService.create(newEventDto, userId);
    }

    //Получение событий, добавленных текущим пользователем
    @GetMapping
    public List<EventShortDto> findAllByInitiator(
            @PathVariable Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /users/{}/events from={}, size={}", userId,from, size);

        return eventService.findAllByInitiator(userId, from, size);
    }

    //Изменение события, добавленного текущим пользователем
    @PatchMapping
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @RequestBody @Valid UpdateEventRequest updateEventRequest) {
        log.info("PATCH /users/{}/events", userId);
        log.info("PATCH /users/{}/events updateEventRequest={}", userId, updateEventRequest);

        return eventService.update(updateEventRequest, userId);
    }

    //Получение полной информации о событии, доабавленном текущим пользователем
    @GetMapping(path = "/{eventId}")
    public EventFullDto findByIdAndInitiator(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{}", userId, eventId);

        return eventService.findByIdAndInitiator(eventId, userId);
    }

    //Отмена события, добавленного текущим пользовтаелем
    @PatchMapping(path = "/{eventId}")
    public EventFullDto cancelEvent(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        log.info("PATCH /users/{}/events/{}", userId, eventId);

        return eventService.cancelEvent(eventId, userId);
    }

    //Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping(path = "/{eventId}/requests")
    public List<RequestDto> findAllRequestsForEvent(@PathVariable Long userId,
                                                    @PathVariable Long eventId) {
        log.info("/GET /{}/events/{}/requests", userId, eventId);

        return requestService.findAllByEventAndOwner(eventId, userId);
    }

    //Подтверждение чужой заявки на участие в событии текущего пользователя
    @PatchMapping(path = "/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmRequest(@PathVariable Long userId, @PathVariable long eventId,
                                     @PathVariable Long reqId) {
        log.info("/PATCH /{}/events/{}/requests/{}/confirm", userId, eventId, reqId);

        return requestService.confirmRequest(userId, eventId, reqId);
    }

    //Отклонение чужой заявки на участие в событии текущего пользователя
    @PatchMapping(path = "/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectRequest(@PathVariable Long userId, @PathVariable long eventId,
                                    @PathVariable Long reqId) {
        log.info("/PATCH /{}/events/{}/requests/{}/reject", userId, eventId, reqId);

        return requestService.rejectRequest(userId, eventId, reqId);
    }
}
