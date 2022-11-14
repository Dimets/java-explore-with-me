package ru.practicum.explorewme.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewme.subscription.model.Subscription;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findBySubscriberIdAndUserId(Long subsId, Long userId);
}
