package ru.practicum.explorewme.eventstate;

import ru.practicum.explorewme.eventstate.model.EventState;

public interface EventStateService {
    EventState findByState(String state);

    EventState findById(Long id);
}
