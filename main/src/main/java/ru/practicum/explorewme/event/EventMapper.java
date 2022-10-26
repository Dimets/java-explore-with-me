package ru.practicum.explorewme.event;

import org.springframework.stereotype.Component;
import ru.practicum.explorewme.category.CategoryMapper;
import ru.practicum.explorewme.category.CategoryService;
import ru.practicum.explorewme.category.dto.CategoryDto;
import ru.practicum.explorewme.event.dto.EventFullDto;
import ru.practicum.explorewme.event.dto.NewEventDto;
import ru.practicum.explorewme.event.model.Event;
import ru.practicum.explorewme.eventstate.EventStateService;
import ru.practicum.explorewme.exception.EntityNotFoundException;
import ru.practicum.explorewme.location.LocationMapper;
import ru.practicum.explorewme.location.dto.LocationDto;
import ru.practicum.explorewme.user.UserMapper;
import ru.practicum.explorewme.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EventMapper {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final LocationMapper locationMapper;


    public EventMapper(CategoryService categoryService, CategoryMapper categoryMapper, UserMapper userMapper,
                       LocationMapper locationMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
        this.locationMapper = locationMapper;
    }

    public Event toEvent(NewEventDto newEventDto, UserDto userDto, CategoryDto categoryDto, LocationDto locationDto)
            throws EntityNotFoundException {
        Event event = new Event();

        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(categoryMapper.toCategory(categoryDto));
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        event.setInitiator(userMapper.toUser(userDto));
        event.setLocation(locationMapper.toLocation(locationDto));
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());

        return event;
    }

    public EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto(
                event.getAnnotation(),
                categoryMapper.toCategoryDto(event.getCategory()),
                0L, //confirmed requests
                event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.getDescription(),
                event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                event.getId(),
                userMapper.toUserShortDto(event.getInitiator()),
                locationMapper.toLocationShortDto(event.getLocation()),
                event.getPaid(),
                event.getParticipantLimit(),
                null,
                event.getRequestModeration(),
                event.getState().getState(),
                event.getTitle()
        );

        if (event.getPublishedOn() != null) {
            eventFullDto.setPublishedOn(event.getPublishedOn()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return eventFullDto;
    }
}
