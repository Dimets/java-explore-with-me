package ru.practicum.explorewme.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewme.request.dto.RequestDto;
import ru.practicum.explorewme.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RequestMapper {
    public RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getCreated(),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus().name());
    }

    public List<RequestDto> toRequestDto(List<Request> requests) {
        return requests.stream()
                .map(x -> toRequestDto(x)).collect(Collectors.toList());
    }

}
