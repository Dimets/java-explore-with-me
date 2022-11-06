package ru.practicum.explorewme.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.exception.EntityNotFoundException;
import ru.practicum.explorewme.exception.ValidationException;
import ru.practicum.explorewme.request.dto.RequestDto;

import java.util.List;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
public class RequestPrivateController {
    private final RequestService requestService;

    //Добавление запроса от текущего пользователя на участие в событии
    @PostMapping(path = "users/{userId}/requests")
    RequestDto create(@PathVariable(name = "userId") Long userId, @RequestParam Long eventId)
            throws ValidationException, EntityNotFoundException {
        log.info("POST /users/{}/requests eventId={}", userId, eventId);

        return requestService.create(userId, eventId);
    }

    //Получение информации о заявках текущего пользоваеля на учасие в чужих событиях
    @GetMapping(path = "users/{userId}/requests")
    List<RequestDto> findAllByRequester(@PathVariable(name = "userId") Long userId) {
        log.info("POST /users/{}/requests", userId);

        return requestService.findAllByRequester(userId);
    }

    //Отмена своего запроса на участие в событии
    @PatchMapping(path = "users/{userId}/requests/{requestId}/cancel")
    RequestDto cancel(@PathVariable(name = "userId") Long userId, @PathVariable(name = "requestId")  Long requestId) throws EntityNotFoundException {
        log.info("/PATCH users/{}/requests/{}/cancel", userId, requestId);

        return requestService.cancel(userId, requestId);
    }

}
