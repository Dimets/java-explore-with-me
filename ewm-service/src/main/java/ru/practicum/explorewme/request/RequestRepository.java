package ru.practicum.explorewme.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewme.request.model.Request;
import ru.practicum.explorewme.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    Integer countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(Long userId);

    Optional<Request> findByIdAndRequesterIdAndStatusIn(Long requestId, Long userId, List<RequestStatus> statuses);

    List<Request> findAllByEventId(Long eventId);

    Optional<Request> findByIdAndStatus(Long requestId, RequestStatus status);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    @Query(value = "select r.* from requests r left join events e on e.id=r.event_id and e.initiator_id=:userId and " +
            "e.id = :eventId", nativeQuery = true)
    List<Request> findAllByEventAndOwner(@Param("userId") Long userId, @Param("eventId") Long eventId);
}
