package ru.practicum.explorewme.event.eventstate;

import ru.practicum.explorewme.event.eventstate.model.EventState;

public interface EventStateService {
    EventState findById(Long id);
}
