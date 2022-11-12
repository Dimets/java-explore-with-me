package ru.practicum.explorewme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.event.dto.AdminUpdateEventRequest;
import ru.practicum.explorewme.event.dto.EventFullDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Slf4j
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventService;

    //Публикация события
    @PatchMapping(path = "/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) {
        log.info("PATCH /admin/events/{}/publish", eventId);

        return eventService.publish(eventId);
    }

    //Отклонение события
    @PatchMapping(path = "/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) {
        log.info("PATCH /admin/events/{}/reject", eventId);

        return eventService.reject(eventId);
    }

    //Поиск событий
    @GetMapping
    public List<EventFullDto> findAll(@RequestParam(required = false) List<Long> users,
                                      @RequestParam(required = false) List<String> states,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(name = "rangeStart", required = false) String start,
                                      @RequestParam(name = "rangeEnd", required = false) String end,
                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                      @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /admin/events?users={}&states={}&categories={}&start={}&end={}&from={}&size={}",
                users, states,categories, start, end, from, size);

        return eventService.findAllAdminByCriteria(users, states, categories, start, end, from, size);
    }

    //Редактирование события
    @PutMapping(path = "/{eventId}")
    public EventFullDto update(@PathVariable Long eventId,
                               @RequestBody @Valid AdminUpdateEventRequest adminUpdateEventRequest) {
        log.info("PUT /admin/events/{}", eventId);
        log.debug("PUT /admin/events/{} adminUpdateEventRequest={}", eventId, adminUpdateEventRequest);

        return eventService.update(eventId, adminUpdateEventRequest);
    }
}
