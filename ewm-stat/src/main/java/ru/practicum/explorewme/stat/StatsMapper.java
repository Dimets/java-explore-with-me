package ru.practicum.explorewme.stat;

import org.springframework.stereotype.Component;
import ru.practicum.explorewme.stat.dto.EndpointHitDto;
import ru.practicum.explorewme.stat.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StatsMapper {
    public Hit toHit(EndpointHitDto endpointHitDto) {
        Hit hit = new Hit();

        hit.setApp(endpointHitDto.getApp());
        hit.setIp(endpointHitDto.getIp());
        hit.setUri(endpointHitDto.getUri());
        hit.setDttm(LocalDateTime.parse(endpointHitDto.getTimestamp(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return hit;
    }


}
