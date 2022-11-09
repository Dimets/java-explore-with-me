package ru.practicum.explorewme.event.eventstate;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewme.event.eventstate.model.EventState;

public interface EventStateRepository extends JpaRepository<EventState, Long> {
    EventState findByState(String state);

}
