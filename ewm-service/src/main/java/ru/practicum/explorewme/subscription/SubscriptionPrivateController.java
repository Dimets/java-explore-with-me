package ru.practicum.explorewme.subscription;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.event.dto.EventShortDto;
import ru.practicum.explorewme.subscription.dto.SubscriptionDto;
import ru.practicum.explorewme.user.dto.UserShortDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/subscriptions")
@Slf4j
@RequiredArgsConstructor
public class SubscriptionPrivateController {
    private final SubscriptionService subscriptionService;

    //Подписка на пользователя
    @PostMapping(path = "/{subsId}/subscribe/{userId}")
    public SubscriptionDto createSubscription(@PathVariable Long subsId, @PathVariable Long userId) {
        log.info("POST /subscriptions/{}/subscribe/{}", subsId, userId);
        return subscriptionService.create(subsId, userId);
    }

    //Получить список пользователей на кого подписан
    @GetMapping(path = "/users/{subsId}")
    public List<UserShortDto> findAllUsers(@PathVariable Long subsId) {
        log.info("POST /subscriptions/users/{}", subsId);

        return subscriptionService.findAllUsers(subsId);
    }

    //Получить список кто на пользователя подписан
    @GetMapping(path = "/subscribers/{userId}")
    public List<UserShortDto> findAllSubscribers(@PathVariable Long userId) {
        log.info("POST /subscriptions/subscribers/{}", userId);

        return subscriptionService.findAllSubscribers(userId);
    }

    //Получить список актуальных событий пользователей, на которых подписан пользователь
    @GetMapping(path = "/events/{subsId}")
    public List<EventShortDto> findAllEvents(@PathVariable Long subsId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /subscriptions/events/{}", subsId);

        return subscriptionService.findAllEvents(subsId, from, size);
    }

    //Получить список актуальных событий пользователя, на которого подписан пользователь
    @GetMapping(path = "/users/{userId}/events/{subsId}")
    public List<EventShortDto> findAllEventsByUser(@PathVariable Long userId, @PathVariable Long subsId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /subscriptions/{}/events/{}", userId, subsId);

        return subscriptionService.findAllEventsByUser(userId, subsId, from, size);
    }

    //Удалить подписку на пользователя
    @PatchMapping(path = "/{subsId}/cancel/{userId}")
    void cancel(@PathVariable Long subsId, @PathVariable Long userId) {
        log.info("PATCH /subscriptions/{}/cancel/{}", subsId, userId);

        subscriptionService.cancel(subsId, userId);
    }

}
