package ru.practicum.explorewme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.event.dto.EventFullDto;
import ru.practicum.explorewme.event.dto.NewEventDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping(path = "/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") Long userId, @RequestBody @Valid NewEventDto newEventDto) throws EntityNotFoundException {
        log.info("POST /users/{}/events", userId);
        log.info("POST /users/{}/events newEventDto={}", userId, newEventDto);

        return eventService.create(newEventDto, userId);
    }

}
