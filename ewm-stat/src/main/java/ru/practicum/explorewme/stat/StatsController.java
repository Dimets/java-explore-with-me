package ru.practicum.explorewme.stat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.stat.dto.EndpointHitDto;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping(path = "/hit")
    public void create(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("POST /hit endpointHitDto={}", endpointHitDto);
        statsService.create(endpointHitDto);
    }

    @GetMapping(path = "/stats")
    public List<ViewStats> findStat(@RequestParam(name = "start", defaultValue = "1991-01-01 00:00:00") String start,
                                       @RequestParam(name = "end", defaultValue = "2099-12-31 23:59:59") String end,
                                       @RequestParam(name = "uris") List<String> uris,
                                       @RequestParam(name = "unique", defaultValue = "false") Boolean unique) throws UnsupportedEncodingException {
        log.info("GET /stats start={} end={} uris={} unique={}", start, end, uris, unique);

        return statsService.viewStats(URLDecoder.decode(start, StandardCharsets.UTF_8.toString()),
                URLDecoder.decode(end, StandardCharsets.UTF_8.toString()), uris, unique);
    }


}
