package ru.practicum.explorewme.compilations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewme.compilations.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query(value = "select * from compilations where (:pinned is null or is_pinned=:pinned)", nativeQuery = true)
    Page<Compilation> findAllByPinned(@Param("pinned") Boolean pinned, Pageable pageable);
}
