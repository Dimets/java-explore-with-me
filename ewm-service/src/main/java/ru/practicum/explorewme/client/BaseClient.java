package ru.practicum.explorewme.client;

import org.springframework.web.client.RestTemplate;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }
}