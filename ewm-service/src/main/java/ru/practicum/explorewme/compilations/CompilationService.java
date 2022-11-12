package ru.practicum.explorewme.compilations;

import ru.practicum.explorewme.compilations.dto.CompilationDto;
import ru.practicum.explorewme.compilations.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto create(NewCompilationDto newCompilationDto);

    void deleteById(Long compId);

    CompilationDto findById(Long compId);

    void deleteEvent(Long compId, Long eventId);

    void addEvent(Long compId, Long eventId);

    void setPinned(Long compId, Boolean flag);

    List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size);
}
