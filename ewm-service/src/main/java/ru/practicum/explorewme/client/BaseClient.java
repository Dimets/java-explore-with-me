package ru.practicum.explorewme.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.explorewme.stat.ViewStatDto;

import java.util.List;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }


    protected ResponseEntity<List<ViewStatDto>> get(String path, @Nullable MultiValueMap<String, String> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> ResponseEntity<List<ViewStatDto>> post(String path, T body) {
        return post(path, null, body);
    }


    protected <T> ResponseEntity<List<ViewStatDto>> post(String path, @Nullable MultiValueMap<String, String> parameters,
                                                       T body) {
        return makeAndSendRequest(HttpMethod.POST, path, parameters, body);
    }

    private <T> ResponseEntity<List<ViewStatDto>> makeAndSendRequest(HttpMethod method, String path,
                                                                   @Nullable MultiValueMap<String, String> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<List<ViewStatDto>> ewmServerResponse;
        ParameterizedTypeReference<List<ViewStatDto>> parameterizedTypeReference = new ParameterizedTypeReference<>() {
        };

        try {
            if (parameters != null) {

                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(path);
                uriBuilder.queryParams(parameters);
                ewmServerResponse = rest.exchange(uriBuilder.toUriString(), method, requestEntity,
                        parameterizedTypeReference);
            } else {
                ewmServerResponse = rest.exchange(path, method, requestEntity, parameterizedTypeReference);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
        return prepareGatewayResponse(ewmServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static <V> ResponseEntity<V> prepareGatewayResponse(ResponseEntity<V> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}