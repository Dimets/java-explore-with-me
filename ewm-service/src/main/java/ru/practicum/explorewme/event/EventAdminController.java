package ru.practicum.explorewme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.event.dto.AdminUpdateEventRequest;
import ru.practicum.explorewme.event.dto.EventFullDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Validated
@Slf4j
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventService;

    @PatchMapping(path = "/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable("eventId") Long eventId) throws EntityNotFoundException {
        log.info("PATCH /admin/events/{}/publish", eventId);

        return eventService.publish(eventId);
    }

    @PatchMapping(path = "/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable("eventId") Long eventId) throws EntityNotFoundException {
        log.info("PATCH /admin/events/{}/reject", eventId);

        return eventService.reject(eventId);
    }

    @GetMapping
    public List<EventFullDto> findAll(@RequestParam(name = "users", required = false) List<Long> users,
                                      @RequestParam(name = "states", required = false) List<String> states,
                                      @RequestParam(name = "categories", required = false) List<Long> categories,
                                      @RequestParam(name = "rangeStart", required = false) String start,
                                      @RequestParam(name = "rangeEnd", required = false) String end,
                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET /admin/events?users={}&states={}&categories={}&start={}&end={}&from={}&size={}",
                users, states,categories, start, end, from, size);

        return eventService.findAllAdminByCriteria(users, states, categories, start, end, from, size);
    }

    @PutMapping(path = "/{eventId}")
    public EventFullDto update(@PathVariable("eventId") Long eventId,
                               @RequestBody @Valid AdminUpdateEventRequest adminUpdateEventRequest)
            throws EntityNotFoundException {
        log.info("PUT /admin/events/{}", eventId);
        log.debug("PUT /admin/events/{} adminUpdateEventRequest={}", eventId, adminUpdateEventRequest);

        return eventService.update(eventId, adminUpdateEventRequest);
    }
}
