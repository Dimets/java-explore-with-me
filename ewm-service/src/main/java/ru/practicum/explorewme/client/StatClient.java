package ru.practicum.explorewme.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewme.stat.HitDto;
import ru.practicum.explorewme.stat.ViewStatDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatClient extends BaseClient{
    private static final String STAT_URL = "http://ewm-stat:9090";

    @Autowired
    public StatClient(RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(STAT_URL))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public void postHit(HitDto hitDto) {
        post(STAT_URL + "/hit", hitDto);
    }

    public List<ViewStatDto> getStats(String start, String end, List<Long> ids, Boolean unique) {

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("start", start);
        parameters.add("end", end);
        parameters.add("unique", unique.toString());

        if (ids != null) {
            for (Long id : ids) {
                parameters.add("uris", "/events/" + id);
            }
        }

        List<ViewStatDto> result = get("/stats", parameters).getBody();
        return (result != null ? result : new ArrayList<>());
    }

    public Long getEventViews(Long eventId) {
        List<ViewStatDto> stats = getStats("1991-01-01 00:00:00","2049-12-31 23:59:59",
        List.of(eventId), false);

        if (stats.size() == 0) {
            return 0L;
        }

        return stats.get(0).getHits();
    }



}
