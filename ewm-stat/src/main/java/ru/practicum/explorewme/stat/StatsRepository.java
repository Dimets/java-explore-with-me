package ru.practicum.explorewme.stat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewme.stat.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    @Query(value = "select app, uri, count(*) as hits from hits where uri in (:uris) " +
            "and dttm between :start and :end group by app, uri", nativeQuery = true)
    List<ViewStats> findByUri(@Param("uris") List<String> uris, @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    @Query(value = "select a.app, a.uri, count(a.*) as hits from (\n" +
            "select app, uri, ip, count(*) as hits from hits where uri in (:uris) " +
            "and dttm between :start and :end group by app, uri, ip) a " +
            "group by  app, uri",
            nativeQuery = true)
    List<ViewStats> findByUriIpUnique(@Param("uris") List<String> uris, @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);
}
