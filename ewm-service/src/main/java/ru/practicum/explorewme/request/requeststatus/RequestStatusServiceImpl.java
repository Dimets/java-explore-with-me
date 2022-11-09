package ru.practicum.explorewme.request.requeststatus;

import org.springframework.stereotype.Service;
import ru.practicum.explorewme.request.requeststatus.model.RequestStatus;

import javax.persistence.EntityNotFoundException;

@Service
public class RequestStatusServiceImpl implements RequestStatusService {
    private final RequestStatusRepository requestStatusRepository;

    public RequestStatusServiceImpl(RequestStatusRepository requestStatusRepository) {
        this.requestStatusRepository = requestStatusRepository;
    }

    @Override
    public RequestStatus findById(Long id) {
        return requestStatusRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("Request status with id=%d not found", id)));
    }
}
