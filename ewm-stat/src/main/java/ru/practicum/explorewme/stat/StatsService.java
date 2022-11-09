package ru.practicum.explorewme.stat;

import ru.practicum.explorewme.stat.dto.EndpointHitDto;

import java.util.List;

public interface StatsService {
    void create(EndpointHitDto endpointHitDto);

    List<ViewStats> viewStats(String start, String end, List<String> uris, Boolean unique);

}
