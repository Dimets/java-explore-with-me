package ru.practicum.explorewme.request.requeststatus;

import ru.practicum.explorewme.request.requeststatus.model.RequestStatus;

public interface RequestStatusService {
    RequestStatus findById(Long id);
}
