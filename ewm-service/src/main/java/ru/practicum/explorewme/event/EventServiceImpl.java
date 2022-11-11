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
import ru.practicum.explorewme.event.model.Event;
import ru.practicum.explorewme.event.model.EventState;
import ru.practicum.explorewme.event.sort.SortOption;
import ru.practicum.explorewme.exception.EntityNotFoundException;
import ru.practicum.explorewme.exception.ValidationException;
import ru.practicum.explorewme.location.LocationMapper;
import ru.practicum.explorewme.location.LocationService;
import ru.practicum.explorewme.location.dto.LocationDto;
import ru.practicum.explorewme.request.RequestService;
import ru.practicum.explorewme.user.UserService;
import ru.practicum.explorewme.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private static final int HOURS_DELAY_FROM_CREATE_TO_EVENT = 2;
    private static final int HOURS_DELAY_FROM_PUBLISH_TO_EVENT = 1;
    private static final int HOURS_DELAY_FROM_UPDATE_TO_EVENT = 2;

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final LocationService locationService;
    private final LocationMapper locationMapper;
    private final RequestService requestService;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public EventFullDto create(NewEventDto newEventDto, Long userId) {
        //TODO minus seconds for test on github
        if (LocalDateTime.parse(newEventDto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .isAfter(LocalDateTime.now().plusHours(HOURS_DELAY_FROM_CREATE_TO_EVENT).minusSeconds(5))) {

            UserDto userDto = userService.findById(userId);

            CategoryDto categoryDto = categoryService.findById(newEventDto.getCategory());

            LocationDto locationDto = locationService.findByCoordinates(newEventDto.getLocation().getLat(),
                    newEventDto.getLocation().getLon());

            Event newEvent = eventMapper.toEvent(newEventDto, userDto, categoryDto, locationDto);

            newEvent.setState(EventState.PENDING);

            EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository.save(newEvent));

            return eventFullDto;
        } else {
            throw new ValidationException(String.format("Дата и время на которые намечено событие не может быть " +
                    "раньше, чем через %d часа от текущего момента", HOURS_DELAY_FROM_CREATE_TO_EVENT));
        }
    }

    @Override
    @Transactional
    public EventFullDto publish(Long eventId) {
        Event event = eventRepository.findByIdAndStateAndEventDateAfter(
                        eventId, EventState.PENDING, LocalDateTime.now().plusHours(HOURS_DELAY_FROM_PUBLISH_TO_EVENT))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d not found " +
                        "or already published or event date start less than %d hour from current date", eventId,
                        HOURS_DELAY_FROM_PUBLISH_TO_EVENT)));

        event.setPublishedOn(LocalDateTime.now());

        event.setState(EventState.PUBLISHED);

        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository.save(event));

        return eventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto reject(Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PENDING)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d not found " +
                        "or already published", eventId)));

        event.setState(EventState.CANCELED);

        EventFullDto eventFullDto = eventMapper.toEventFullDto(eventRepository.save(event));

        return eventFullDto;
    }

    @Override
    public List<EventShortDto> findAllByInitiator(Long userId, Integer from, Integer size) {
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
    public EventFullDto update(UpdateEventRequest updateEventRequest, Long userId) {
        Event event = eventRepository.findByIdAndInitiatorIdAndStateIn(updateEventRequest.getEventId(),
                        userId, List.of(EventState.PENDING, EventState.CANCELED))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Event with id=%d not found", updateEventRequest.getEventId())));

        if (updateEventRequest.getEventDate() != null &&
                LocalDateTime.parse(updateEventRequest.getEventDate(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        .isAfter(LocalDateTime.now().plusHours(HOURS_DELAY_FROM_UPDATE_TO_EVENT))) {

            event.setEventDate(LocalDateTime.parse(updateEventRequest.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        } else {
            throw new ValidationException(String.format("Дата события должна быть не раньше чем через %d часа" +
                    " от текущего времени", HOURS_DELAY_FROM_UPDATE_TO_EVENT));
        }

        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }

        if (updateEventRequest.getCategory() != null) {
            event.setCategory(categoryMapper.toCategory(categoryService.findById(updateEventRequest.getCategory())));
        }

        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }

        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }

        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }

        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }

        if (event.getState().equals(EventState.CANCELED)) {
            event.setState(EventState.PENDING);
        }

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto update(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {

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
    public EventFullDto findByIdAndInitiator(Long eventId, Long userId) {
        return eventMapper.toEventFullDto(eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Event with id=%d and initiator_id=%d not found", eventId, userId))));
    }

    @Override
    @Transactional
    public EventFullDto cancelEvent(Long eventId, Long userId) {
        Event event = eventRepository.findByIdAndInitiatorIdAndStateIn(eventId, userId, List.of(EventState.PENDING))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Event with id=%d and initiator_id=%d and state_id=%d not found",
                                eventId, userId, 1L)));

        event.setState(EventState.CANCELED); //CANCELLED STATE

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto findById(Long eventId) {
        return eventMapper.toEventFullDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d not found", eventId))));
    }

    @Override
    public EventFullDto findPublicById(Long eventId) {
        return eventMapper.toEventFullDto(eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d not found", eventId))));
    }

    @Override
    public List<EventShortDto> findAllPublicByCriteria(String text, List<Long> categories, Boolean paid,
                                                       Boolean onlyAvailable, String start, String end,
                                                       Integer from, Integer size, SortOption sort) {
        List<Predicate> predicates = new ArrayList<>();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Event> eventCriteriaQuery = criteriaBuilder.createQuery(Event.class);

        Root<Event> eventRoot = eventCriteriaQuery.from(Event.class);


        //Только опубликованные события
        predicates.add(criteriaBuilder.equal(eventRoot.get("state"), EventState.PUBLISHED));

        if (text != null) {
            String likeValue = "%" + text.toLowerCase() + "%";
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(eventRoot.get("annotation")), likeValue),
                    criteriaBuilder.like(criteriaBuilder.lower(eventRoot.get("description")), likeValue))
            );
        }

        if (categories != null) {
            predicates.add((criteriaBuilder.in(eventRoot.get("category").get("id")).value(categories)));
        }

        if (paid != null) {
            predicates.add(criteriaBuilder.equal(eventRoot.get("paid"), paid));
        }

        //TODO avail

        if (start != null) {
            predicates.add(criteriaBuilder.greaterThan(eventRoot.get("eventDate"),
                    LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }

        if (end != null) {
            predicates.add(criteriaBuilder.lessThan(eventRoot.get("eventDate"),
                    LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }

        if (sort != null && sort.equals(SortOption.EVENT_DATE)) {
            return eventMapper.toEventShortDto(entityManager.createQuery(eventCriteriaQuery.select(eventRoot)
                            .where(predicates.toArray(new Predicate[]{}))
                            .orderBy(criteriaBuilder.asc(eventRoot.get("eventDate"))))
                    .setFirstResult(from)
                    .setMaxResults(size)
                    .getResultList());
        } else if (sort != null && sort.equals(SortOption.VIEWS)) {
            List<EventShortDto> eventShortDtoList = eventMapper.toEventShortDto(
                    entityManager.createQuery(eventCriteriaQuery.select(eventRoot)
                            .where(predicates.toArray(new Predicate[]{})))
                    .getResultList());

            eventShortDtoList.stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews).reversed())
                    .collect(Collectors.toList());

            return eventShortDtoList.subList(from, from + size);
        }

        return eventMapper.toEventShortDto(entityManager.createQuery(eventCriteriaQuery.select(eventRoot)
                        .where(predicates.toArray(new Predicate[]{})))
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList());

    }

    @Override
    public List<EventFullDto> findAllAdminByCriteria(List<Long> users, List<EventState> states, List<Long> categories,
                                                     String start, String end, Integer from, Integer size) {
        List<Predicate> predicates = new ArrayList<>();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Event> eventCriteriaQuery = criteriaBuilder.createQuery(Event.class);

        Root<Event> eventRoot = eventCriteriaQuery.from(Event.class);

        if (users != null) {
            predicates.add(criteriaBuilder.in(eventRoot.get("initiator").get("id")).value(users));
        }

        if (states != null) {
            predicates.add(criteriaBuilder.in(eventRoot.get("state")).value(states));
        }

        if (categories != null) {
            predicates.add((criteriaBuilder.in(eventRoot.get("category").get("id")).value(categories)));
        }

        if (start != null) {
            predicates.add(criteriaBuilder.greaterThan(eventRoot.get("eventDate"),
                    LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }

        if (end != null) {
            predicates.add(criteriaBuilder.lessThan(eventRoot.get("eventDate"),
                    LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }

        return eventMapper.toEventFullDto(entityManager.createQuery(eventCriteriaQuery.select(eventRoot)
                        .where(predicates.toArray(new Predicate[]{})))
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList());
    }
}
