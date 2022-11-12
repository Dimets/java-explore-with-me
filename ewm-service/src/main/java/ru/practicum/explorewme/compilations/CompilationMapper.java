package ru.practicum.explorewme.compilations;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewme.compilations.dto.CompilationDto;
import ru.practicum.explorewme.compilations.dto.NewCompilationDto;
import ru.practicum.explorewme.compilations.model.Compilation;
import ru.practicum.explorewme.event.EventMapper;
import ru.practicum.explorewme.event.model.Event;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        Compilation compilation = new Compilation();

        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setEvents(Set.copyOf(events));

        return compilation;
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();

        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setEvents(eventMapper.toEventShortDto(List.copyOf(compilation.getEvents())));

        return compilationDto;
    }

    public List<CompilationDto> compilationDto(List<Compilation> compilations) {
        return compilations.stream()
                .map(x -> toCompilationDto(x)).collect(Collectors.toList());
    }

}
