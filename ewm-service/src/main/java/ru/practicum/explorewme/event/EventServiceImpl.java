package ru.practicum.explorewme.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewme.category.CategoryMapper;
import ru.practicum.explorewme.category.CategoryService;
import ru.practicum.explorewme.category.dto.CategoryDto;
import ru.practicum.explorewme.event.dto.*;
import ru.practicum.explorewme.event.eventstate.EventStateService;
import ru.practicum.explorewme.event.model.Event;
import ru.practicum.explorewme.exception.EntityNotFoundException;
import ru.practicum.explorewme.location.LocationMapper;
import ru.practicum.explorewme.location.LocationService;
import ru.practicum.explorewme.location.dto.LocationDto;
import ru.practicum.explorewme.request.RequestService;
import ru.practicum.explorewme.user.UserService;
import ru.practicum.explorewme.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final LocationService locationService;
    private final LocationMapper locationMapper;
    private final EventStateService eventStateService;
    private final RequestService requestService;

    final List<String> SORTING = List.of("EVENT_DATE", "VIEWS"); //варианты сортировки

    @Override
    @Transactional
    public EventFullDto create(NewEventDto newEventDto, Long userId) throws EntityNotFoundException {
        UserDto userDto = userService.findById(userId);

        CategoryDto categoryDto = categoryService.findById(newEventDto.getCategory());

        LocationDto locationDto = locationService.findByCoordinates(newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon());

        Event newEvent = eventMapper.toEvent(newEventDto, userDto, categoryDto, locationDto);

        newEvent.setState(eventStateService.findById(1L)); //PENDING when create event

        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository.save(newEvent));

        eventFullDto.setConfirmedRequests(0L);

        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto publish(Long eventId) throws EntityNotFoundException {
        Event event = eventRepository.findByIdAndStateIdAndEventDateAfter(
                        eventId, 1L, LocalDateTime.now().plusHours(1))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d not found " +
                        "or already published or event date start less than 1 hour from current date", eventId)));

        event.setPublishedOn(LocalDateTime.now());

        event.setState(eventStateService.findById(2L)); //PUBLISHED state

        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository.save(event));

        eventFullDto.setConfirmedRequests(0L);

        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto reject(Long eventId) throws EntityNotFoundException {
        Event event = eventRepository.findByIdAndStateId(eventId, 1L)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d not found " +
                        "or already published", eventId)));

        event.setState(eventStateService.findById(3L)); //CANCELLED STATE

        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository.save(event));

        eventFullDto.setConfirmedRequests(0L);

        return eventFullDto;
    }

    @Override
    public List<EventShortDto> findAllByInitiator(Long userId, Integer from, Integer size) {
        userService.findById(userId);

        Pageable pageable = PageRequest.of(from / size, size);

        List<EventShortDto> eventShortDtoList = eventMapper.toEventShortDto(
                eventRepository.findByInitiatorId(userId, pageable).stream()
                        .collect(Collectors.toList()));

        eventShortDtoList.stream()
                .forEach((x) -> x.setConfirmedRequests(requestService.getConfirmedRequestsCount(x.getId())));

        return eventShortDtoList;
    }

    @Override
    @Transactional
    public EventFullDto update(UpdateEventRequest updateEventRequest, Long userId) throws EntityNotFoundException {
        userService.findById(userId);

        Event event = eventRepository.findById(updateEventRequest.getEventId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Event with id=%d not found", updateEventRequest.getEventId())));

        event.setAnnotation(updateEventRequest.getAnnotation());
        event.setCategory(categoryMapper.toCategory(categoryService.findById(updateEventRequest.getCategory())));
        event.setDescription(updateEventRequest.getDescription());
        event.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        event.setPaid(updateEventRequest.getPaid());
        event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        event.setTitle(updateEventRequest.getTitle());

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto update(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest)
            throws EntityNotFoundException {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d not found", eventId)));

        if (adminUpdateEventRequest.getAnnotation() != null) {
            event.setAnnotation(adminUpdateEventRequest.getAnnotation());
        }

        if (adminUpdateEventRequest.getCategory() != null) {
            event.setCategory(categoryMapper.toCategory(
                    categoryService.findById(adminUpdateEventRequest.getCategory())));
        }

        if (adminUpdateEventRequest.getDescription() != null) {
            event.setDescription(adminUpdateEventRequest.getDescription());
        }

        if (adminUpdateEventRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(adminUpdateEventRequest.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (adminUpdateEventRequest.getLocation() != null) {
            event.setLocation(locationMapper.toLocation(adminUpdateEventRequest.getLocation()));
        }

        if (adminUpdateEventRequest.getPaid() != null) {
            event.setPaid(adminUpdateEventRequest.getPaid());
        }

        event.setParticipantLimit(adminUpdateEventRequest.getParticipantLimit());

        if (adminUpdateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(adminUpdateEventRequest.getRequestModeration());
        }

        if (adminUpdateEventRequest.getTitle() != null) {
            event.setTitle(adminUpdateEventRequest.getTitle());
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto findByIdAndInitiator(Long eventId, Long userId) throws EntityNotFoundException {
        return eventMapper.toEventFullDto(eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Event with id=%d and initiator_id=%d not found", eventId, userId))));
    }

    @Override
    @Transactional
    public EventFullDto cancelEvent(Long eventId, Long userId) throws EntityNotFoundException {
        Event event = eventRepository.findByIdAndInitiatorIdAndStateId(eventId, userId, 1L)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Event with id=%d and initiator_id=%d and state_id=%d not found",
                                eventId, userId, 1L)));

        event.setState(eventStateService.findById(3L)); //CANCELLED STATE

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto findById(Long eventId) throws EntityNotFoundException {
        return eventMapper.toEventFullDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d not found", eventId))));
    }

    @Override
    public EventFullDto findPublicById(Long eventId) throws EntityNotFoundException {
        return eventMapper.toEventFullDto(eventRepository.findByIdAndStateId(eventId, 2L)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d not found", eventId))));
    }

    @Override
    public List<EventShortDto> findEventsByCriteria(String text, List<Integer> categories, Boolean paid,
                                                    String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                    String sort, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        if (onlyAvailable) {
            return eventMapper.toEventShortDto(eventRepository.findAvailableEventsByCriteria(text.toLowerCase(),
                            categories, paid,
                            LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            sort, pageable).stream()
                    .collect(Collectors.toList()));
        }

        return eventMapper.toEventShortDto(eventRepository.findAllEventsByCriteria(text.toLowerCase(), categories, paid,
                        LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        sort, pageable).stream()
                .collect(Collectors.toList()));
    }

    @Override
    public List<EventFullDto> findAll(List<Long> users, List<String> states, List<Long> categories, String start,
                                      String end, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        log.info("original start=" + start);

        LocalDateTime startRange = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LocalDateTime endRange = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return eventMapper.toEventFullDto(eventRepository.findAll(users, states, categories, startRange, endRange,
                        pageable).stream()
                .collect(Collectors.toList()));
    }
}
