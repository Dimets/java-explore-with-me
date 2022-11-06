package ru.practicum.explorewme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.client.StatClient;
import ru.practicum.explorewme.event.dto.EventFullDto;
import ru.practicum.explorewme.event.dto.EventShortDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;
import ru.practicum.explorewme.stat.HitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Validated
@Slf4j
@RequiredArgsConstructor
public class EventPublicController {
    private final EventService eventService;
    private final StatClient statClient;


    //Получение событий с возможностью фильтрации//
    @GetMapping
    public List<EventShortDto> findEventsByCriteria(
            @RequestParam String text, @RequestParam List<Integer> categories, @RequestParam Boolean paid,
            @RequestParam String rangeStart, @RequestParam String rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam String sort, @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size,  HttpServletRequest request) {

        log.info("GET /events text={} categories={} paid={} rangeStart={} rangeEnd={} onlyAvailable={} sort={} " +
                "from={} size={}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        statClient.postHit(new HitDto("ewm-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        return eventService.findEventsByCriteria(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size);
    }


    //Получение подробной информации об опубликованном событии по его идентификатору
    @GetMapping(path = "/{id}")
    public EventFullDto findEvent (@PathVariable(name = "id") Long eventId, HttpServletRequest request)
            throws EntityNotFoundException {

        log.info("GET /events/{}", eventId);

        statClient.postHit(new HitDto("ewm-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        return eventService.findPublicById(eventId);

    }
}
