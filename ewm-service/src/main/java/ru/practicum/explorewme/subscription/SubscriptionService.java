package ru.practicum.explorewme.subscription;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.explorewme.event.dto.EventShortDto;
import ru.practicum.explorewme.subscription.dto.SubscriptionDto;
import ru.practicum.explorewme.user.dto.UserShortDto;

import java.util.List;

public interface SubscriptionService {
    SubscriptionDto create(Long subsId, Long userId);

    List<UserShortDto> findAllUsers(Long subsId);

    List<UserShortDto> findAllSubscribers(Long userId);

    List<EventShortDto> findAllEvents(Long subsId, Integer from, Integer size);

    List<EventShortDto> findAllEventsByUser(Long userId, Long subsId, Integer from, Integer size);

    void cancel(Long subsId, Long userId);


}
