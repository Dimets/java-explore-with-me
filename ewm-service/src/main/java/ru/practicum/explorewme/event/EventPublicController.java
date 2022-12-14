package ru.practicum.explorewme.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.client.StatClient;
import ru.practicum.explorewme.event.dto.EventFullDto;
import ru.practicum.explorewme.event.dto.EventShortDto;
import ru.practicum.explorewme.event.sort.SortOption;
import ru.practicum.explorewme.stat.HitDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Slf4j
@RequiredArgsConstructor
public class EventPublicController {
    private final EventService eventService;
    private final StatClient statClient;


    //Получение событий с возможностью фильтрации//
    @GetMapping
    public List<EventShortDto> findEventsByCriteria(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(name = "sort", required = false) SortOption sortOption,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {

        log.info("GET /events text={} categories={} paid={} rangeStart={} rangeEnd={} onlyAvailable={} sort={} " +
                "from={} size={}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sortOption, from, size);

        statClient.postHit(new HitDto("ewm-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        return eventService.findAllPublicByCriteria(text, categories, paid, onlyAvailable, rangeStart,
                rangeEnd, from, size, sortOption);
    }


    //Получение подробной информации об опубликованном событии по его идентификатору
    @GetMapping(path = "/{eventId}")
    public EventFullDto findEvent(@PathVariable Long eventId, HttpServletRequest request) {

        log.info("GET /events/{}", eventId);

        statClient.postHit(new HitDto("ewm-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        return eventService.findPublicById(eventId);
    }
}
