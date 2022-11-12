package ru.practicum.explorewme.request;

import ru.practicum.explorewme.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(Long userId, Long eventId);

    Integer getConfirmedRequestsCount(Long eventId);

    List<RequestDto> findAllByRequester(Long userId);

    RequestDto cancel(Long userId, Long eventId);

    RequestDto reject(Long requestId);

    List<RequestDto> findAllByEvent(Long eventId);

    List<RequestDto> findAllByEventAndOwner(Long eventId, Long userId);

    RequestDto confirm(Long requestId);

    RequestDto confirmRequest(Long userId, Long eventId, Long requestId);

    RequestDto rejectRequest(Long userId, Long eventId, Long requestId);
}
