package ru.practicum.explorewme.event.eventstate;

import org.springframework.stereotype.Service;
import ru.practicum.explorewme.event.eventstate.model.EventState;

import javax.persistence.EntityNotFoundException;

@Service
public class EventStateServiceImpl implements EventStateService {
    private final EventStateRepository eventStateRepository;

    public EventStateServiceImpl(EventStateRepository eventStateRepository) {
        this.eventStateRepository = eventStateRepository;
    }

    @Override
    public EventState findById(Long id) {
        return eventStateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event state with id=%d not found", id)));
    }
}
