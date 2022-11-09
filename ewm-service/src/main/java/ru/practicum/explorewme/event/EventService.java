package ru.practicum.explorewme.event;

import ru.practicum.explorewme.event.dto.*;
import ru.practicum.explorewme.event.sort.SortOption;
import ru.practicum.explorewme.exception.EntityNotFoundException;

import java.util.List;

public interface EventService {
    EventFullDto create(NewEventDto newEventDto, Long userId) throws EntityNotFoundException;

    EventFullDto publish(Long eventId) throws EntityNotFoundException;

    EventFullDto reject(Long eventId) throws EntityNotFoundException;

    List<EventShortDto> findAllByInitiator(Long userId, Integer from, Integer size);

    EventFullDto update(UpdateEventRequest updateEventRequest, Long userId) throws EntityNotFoundException;

    EventFullDto update(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest) throws EntityNotFoundException;

    EventFullDto findByIdAndInitiator(Long eventId, Long userId) throws EntityNotFoundException;

    EventFullDto cancelEvent(Long eventId, Long userId) throws EntityNotFoundException;

    EventFullDto findById(Long eventId) throws EntityNotFoundException;

    EventFullDto findPublicById(Long eventId) throws EntityNotFoundException;

    List<EventFullDto> findAllAdminByCriteria(List<Long> users, List<String> states, List<Long> categories,
                                         String start, String end, Integer from, Integer size);

    List<EventShortDto> findAllPublicByCriteria(String text, List<Long> categories, Boolean paid, Boolean onlyAvailable,
                                         String start, String end, Integer from, Integer size, SortOption sort);

}

