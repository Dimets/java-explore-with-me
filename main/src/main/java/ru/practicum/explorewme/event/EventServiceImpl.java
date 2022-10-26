package ru.practicum.explorewme.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewme.category.CategoryService;
import ru.practicum.explorewme.category.dto.CategoryDto;
import ru.practicum.explorewme.event.dto.EventFullDto;
import ru.practicum.explorewme.event.dto.NewEventDto;
import ru.practicum.explorewme.event.model.Event;
import ru.practicum.explorewme.eventstate.EventStateService;
import ru.practicum.explorewme.exception.EntityNotFoundException;
import ru.practicum.explorewme.location.LocationService;
import ru.practicum.explorewme.location.dto.LocationDto;
import ru.practicum.explorewme.user.UserService;
import ru.practicum.explorewme.user.dto.UserDto;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final EventStateService eventStateService;

    @Override
    @Transactional
    public EventFullDto create(NewEventDto newEventDto, Long userId) throws EntityNotFoundException {
        UserDto userDto = userService.findById(userId);

        CategoryDto categoryDto = categoryService.findById(newEventDto.getCategory());

        LocationDto locationDto = locationService.findByCoordinates(newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon());

        Event newEvent = eventMapper.toEvent(newEventDto, userDto, categoryDto, locationDto);

        newEvent.setState(eventStateService.findById(1L)); //PENDING when create event

        return eventMapper.toEventFullDto(eventRepository.save(newEvent));
    }
}
