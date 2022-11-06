package ru.practicum.explorewme.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewme.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    @Query(value = "select count(*) from requests where event_id=?1 and status_id=2", nativeQuery = true)
    Long findConfirmedRequestsCount(Long eventId);

    List<Request> findAllByRequesterId(Long userId);

    @Query(value = "select * from requests where id=?1 and requester_id=?2 and status_id in (1, 2)", nativeQuery = true)
    Optional<Request> findOwnForCancel(Long requestId, Long userId);

    List<Request> findAllByEventId(Long eventId);

    Optional<Request> findByIdAndStatusId(Long requestId, Long statusId);

    List<Request> findAllByEventIdAndStatusId(Long eventId, Long statusId);
}
