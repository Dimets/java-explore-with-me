package ru.practicum.explorewme.compilations;

import ru.practicum.explorewme.compilations.dto.CompilationDto;
import ru.practicum.explorewme.compilations.dto.NewCompilationDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;

import java.util.List;

public interface CompilationService {
    CompilationDto create(NewCompilationDto newCompilationDto);

    void deleteById(Long compId);

    CompilationDto findById(Long compId) throws EntityNotFoundException;

    void deleteEvent(Long compId, Long eventId) throws EntityNotFoundException;

    void addEvent (Long compId, Long eventId) throws EntityNotFoundException;

    void setPinned(Long compId, Boolean flag) throws EntityNotFoundException;

    List<CompilationDto> findCompilations (Boolean pinned, Integer from, Integer size);
}
