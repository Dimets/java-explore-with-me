package ru.practicum.explorewme.subscription;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewme.subscription.dto.SubscriptionDto;
import ru.practicum.explorewme.subscription.model.Subscription;
import ru.practicum.explorewme.user.UserMapper;

@Component
@AllArgsConstructor
public class SubscriptionMapper {
    private final UserMapper userMapper;

    SubscriptionDto toSubscriptionDto(Subscription subscription) {
        return new SubscriptionDto(
                userMapper.toUserShortDto(subscription.getSubscriber()),
                userMapper.toUserShortDto(subscription.getUser()),
                subscription.getStatus().name()
        );
    }
}
