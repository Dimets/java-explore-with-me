package ru.practicum.explorewme.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explorewme.stat.HitDto;
import ru.practicum.explorewme.stat.ListViewStatDto;
import ru.practicum.explorewme.stat.ViewStatDto;

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
        HttpEntity<HitDto> request = new HttpEntity<HitDto>(hitDto);
        this.rest.postForLocation(STAT_URL + "/hit", request);
    }
//TODO

    public List<ViewStatDto> getHits(String uri) {

        return this.rest.getForObject(STAT_URL + "/stats?uris=" + uri + "&unique=false", ListViewStatDto.class).getViewStatDtos();
    }

}
