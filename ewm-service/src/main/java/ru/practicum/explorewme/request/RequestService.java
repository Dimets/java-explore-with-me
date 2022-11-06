package ru.practicum.explorewme.request;

import ru.practicum.explorewme.exception.EntityNotFoundException;
import ru.practicum.explorewme.exception.ValidationException;
import ru.practicum.explorewme.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(Long userId, Long eventId) throws EntityNotFoundException, ValidationException;

    Long getConfirmedRequestsCount(Long eventId);

    List<RequestDto> findAllByRequester(Long userId);

    RequestDto cancel(Long userId, Long eventId) throws EntityNotFoundException;

    RequestDto reject(Long requestId) throws EntityNotFoundException;

    List<RequestDto> findAllByEvent(Long eventId);

    RequestDto confirm(Long requestId) throws EntityNotFoundException;

    RequestDto confirmRequest(Long userId, Long eventId, Long requestId) throws EntityNotFoundException;

    RequestDto rejectRequest(Long userId, Long eventId, Long requestId) throws EntityNotFoundException;
}
