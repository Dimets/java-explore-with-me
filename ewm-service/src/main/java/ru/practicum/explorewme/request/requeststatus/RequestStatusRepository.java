package ru.practicum.explorewme.request.requeststatus;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewme.request.requeststatus.model.RequestStatus;

public interface RequestStatusRepository extends JpaRepository<RequestStatus, Long> {
}
