package ru.practicum.explorewme.event;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewme.event.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
