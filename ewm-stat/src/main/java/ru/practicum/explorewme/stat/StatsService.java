package ru.practicum.explorewme.stat;

import ru.practicum.explorewme.stat.dto.EndpointHitDto;

import java.util.List;

public interface StatsService {
    void create(EndpointHitDto endpointHitDto);

    List<ViewStats> viewStats(String start, String End,List<String> uris, Boolean unique);

}
