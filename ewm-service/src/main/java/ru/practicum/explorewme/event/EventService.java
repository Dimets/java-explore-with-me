package ru.practicum.explorewme.event;

import ru.practicum.explorewme.event.dto.*;
import ru.practicum.explorewme.event.sort.SortOption;

import java.util.List;

public interface EventService {
    EventFullDto create(NewEventDto newEventDto, Long userId);

    EventFullDto publish(Long eventId);

    EventFullDto reject(Long eventId);

    List<EventShortDto> findAllByInitiator(Long userId, Integer from, Integer size);

    EventFullDto update(UpdateEventRequest updateEventRequest, Long userId);

    EventFullDto update(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest);

    EventFullDto findByIdAndInitiator(Long eventId, Long userId);

    EventFullDto cancelEvent(Long eventId, Long userId);

    EventFullDto findById(Long eventId);

    EventFullDto findPublicById(Long eventId);

    List<EventFullDto> findAllAdminByCriteria(List<Long> users, List<String> states, List<Long> categories,
                                              String start, String end, Integer from, Integer size);

    List<EventShortDto> findAllPublicByCriteria(String text, List<Long> categories, Boolean paid, Boolean onlyAvailable,
                                         String start, String end, Integer from, Integer size, SortOption sort);

}

