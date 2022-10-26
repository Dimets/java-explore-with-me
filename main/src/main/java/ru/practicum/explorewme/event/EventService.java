package ru.practicum.explorewme.event;

import ru.practicum.explorewme.event.dto.EventFullDto;
import ru.practicum.explorewme.event.dto.NewEventDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;

public interface EventService {
    EventFullDto create(NewEventDto newEventDto, Long userId) throws EntityNotFoundException;
}
