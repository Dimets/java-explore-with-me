package ru.practicum.explorewme.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewme.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndStateIdAndEventDateAfter(Long eventId, Long stateId, LocalDateTime time);

    Optional<Event> findByIdAndStateId(Long eventId, Long stateId);

    Page<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    Optional<Event> findByIdAndInitiatorIdAndStateId(Long eventId, Long initiatorId, Long stateId);

    @Query(value = "select e.* from events e " +
            "where (lower(e.annotation) like %:text% or lower(e.description) like %:text%) " +
            " and e.category_id in (:categories) and e.is_paid=:paid and e.event_date > :start and e.event_date < :end " +
            "and e.participiant_limit < (select count(*) from requests where event_id=e.event_id and status_id=2) " +
            "order by :sort",
            nativeQuery = true)
    Page<Event> findAvailableEventsByCriteria(@Param("text") String text, @Param("categories") List<Integer> categories,
                                     @Param("paid") Boolean paid, @Param("start") LocalDateTime rangeStart,
                                     @Param("end") LocalDateTime rangeEnd, @Param("sort") String sort, Pageable pageable);

    @Query(value = "select e.* from events e " +
            "where (lower(e.annotation) like %:text% or lower(e.description) like %:text%) " +
            "and e.category_id in (:categories) and e.is_paid=:paid and e.event_date > :start and e.event_date < :end " +
            "order by :sort",
            nativeQuery = true)
    Page<Event> findAllEventsByCriteria(@Param("text") String text, @Param("categories") List<Integer> categories,
                                              @Param("paid") Boolean paid, @Param("start") LocalDateTime rangeStart,
                                              @Param("end") LocalDateTime rangeEnd, @Param("sort") String sort,
                                        Pageable pageable);

    @Query(value = "select e.* from events e left join event_states es on e.state_id=es.id " +
            "where (:users is null or e.initiator_id in (:users)) " +
            "and (:states is null or es.state in (:states)) and (:catId is null or e.category_id in (:catId))" +
            "and (e.event_date > :start) and (e.event_date < :end)",
            nativeQuery = true)
    Page<Event> findAll(@Param("users") List<Long> users, @Param("states") List<String> states,
                        @Param("catId") List<Long> categories, @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end, Pageable pageable);

    @Query(value = "select e.* from events e " +
            "where (:users is null or e.initiator_id in (:users)) ",
            nativeQuery = true)
    List<Event> findAllTmp(@Param("users") List<Long> users);
}

