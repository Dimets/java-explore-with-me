package ru.practicum.explorewme.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewme.event.model.Event;
import ru.practicum.explorewme.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndStateAndEventDateAfter(Long eventId, EventState eventState, LocalDateTime time);

    Optional<Event> findByIdAndState(Long eventId, EventState state);

    Page<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    Optional<Event> findByIdAndInitiatorIdAndStateIn(Long eventId, Long initiatorId, List<EventState> states);

}

