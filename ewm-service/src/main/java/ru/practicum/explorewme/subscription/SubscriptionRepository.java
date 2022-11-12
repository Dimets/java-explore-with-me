package ru.practicum.explorewme.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewme.subscription.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

}
