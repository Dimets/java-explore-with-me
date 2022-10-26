package ru.practicum.explorewme.eventstate;

import org.springframework.stereotype.Service;
import ru.practicum.explorewme.eventstate.model.EventState;

import javax.persistence.EntityNotFoundException;

@Service
public class EventStateServiceImpl implements EventStateService {
    private final EventStateRepository eventStateRepository;

    public EventStateServiceImpl(EventStateRepository eventStateRepository) {
        this.eventStateRepository = eventStateRepository;
    }

    @Override
    public EventState findByState(String state) {
        return eventStateRepository.findByState(state);
    }

    @Override
    public EventState findById(Long id) {
        return eventStateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event state with id=%d not found", id)));
    }
}
