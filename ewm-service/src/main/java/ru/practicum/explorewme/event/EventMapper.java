package ru.practicum.explorewme.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.explorewme.category.CategoryMapper;
import ru.practicum.explorewme.category.dto.CategoryDto;
import ru.practicum.explorewme.client.StatClient;
import ru.practicum.explorewme.event.dto.EventFullDto;
import ru.practicum.explorewme.event.dto.EventShortDto;
import ru.practicum.explorewme.event.dto.NewEventDto;
import ru.practicum.explorewme.event.model.Event;
import ru.practicum.explorewme.location.LocationMapper;
import ru.practicum.explorewme.location.dto.LocationDto;
import ru.practicum.explorewme.request.RequestService;
import ru.practicum.explorewme.user.UserMapper;
import ru.practicum.explorewme.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {
    private CategoryMapper categoryMapper;
    private UserMapper userMapper;
    private LocationMapper locationMapper;
    private RequestService requestService;
    private StatClient statClient;


    @Autowired
    public EventMapper(CategoryMapper categoryMapper, UserMapper userMapper, LocationMapper locationMapper,
                       @Lazy RequestService requestService, StatClient statClient) {
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
        this.locationMapper = locationMapper;
        this.requestService = requestService;
        this.statClient = statClient;
    }

    public Event toEvent(NewEventDto newEventDto, UserDto userDto, CategoryDto categoryDto, LocationDto locationDto) {
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

    public Event toEvent(EventFullDto eventFullDto, UserDto userDto) {
        Event event = new Event();

        event.setId(eventFullDto.getId());
        event.setAnnotation(eventFullDto.getAnnotation());
        event.setCategory(categoryMapper.toCategory(eventFullDto.getCategory()));
        event.setDescription(eventFullDto.getDescription());
        event.setEventDate(LocalDateTime.parse(eventFullDto.getEventDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        event.setInitiator(userMapper.toUser(userDto));
        return event;
    }



    public EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
        eventFullDto.setCreatedOn(event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventFullDto.setId(event.getId());
        eventFullDto.setInitiator(userMapper.toUserShortDto(event.getInitiator()));
        eventFullDto.setLocation(locationMapper.toLocationShortDto(event.getLocation()));
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState().getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setConfirmedRequests(requestService.getConfirmedRequestsCount(event.getId()));
        //TODO views

        if (event.getPublishedOn() != null) {
            eventFullDto.setPublishedOn(event.getPublishedOn()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return eventFullDto;
    }

    public List<EventFullDto> toEventFullDto(List<Event> events) {
        return events.stream().map(x -> toEventFullDto(x)).collect(Collectors.toList());
    }


    public EventShortDto toEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();

        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setEventDate(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        eventShortDto.setId(event.getId());
        eventShortDto.setInitiator(userMapper.toUserShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setConfirmedRequests(requestService.getConfirmedRequestsCount(event.getId()));
        //TODO views


        return eventShortDto;
    }

    public List<EventShortDto> toEventShortDto(List<Event> events) {
        return events.stream().map(x -> toEventShortDto(x)).collect(Collectors.toList());
    }

}
