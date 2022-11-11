package ru.practicum.explorewme.compilations;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewme.compilations.dto.CompilationDto;
import ru.practicum.explorewme.compilations.dto.NewCompilationDto;
import ru.practicum.explorewme.compilations.model.Compilation;
import ru.practicum.explorewme.event.EventRepository;
import ru.practicum.explorewme.event.model.Event;
import ru.practicum.explorewme.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> events = newCompilationDto.getEvents().stream()
                .map(x -> eventRepository.findById(x).get()).collect(Collectors.toList());

        events = validateListEvents(events);

        return compilationMapper.toCompilationDto(
                compilationRepository.save(compilationMapper.toCompilation(newCompilationDto, events)));
    }

    @Override
    @Transactional
    public void deleteById(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto findById(Long compId) {
        return compilationMapper.toCompilationDto(compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Подборка с id=%d не найдена", compId))));
    }

    @Override
    @Transactional
    public void deleteEvent(Long compId, Long eventId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Подборка с id=%d не найдена", compId)));

        Set<Event> events = compilation.getEvents();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Удаляемое событие с id=%d не найдено")));

        if (events.remove(event)) {
            compilation.setEvents(events);

            compilationRepository.save(compilation);
        } else {
            throw new EntityNotFoundException(String.format("Событие с id=%d не найдено в подборке с id=%d",
                    eventId, compId));
        }

    }

    @Override
    @Transactional
    public void addEvent(Long compId, Long eventId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Подборка с id=%d не найдена", compId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Добавляемое событие с id=%d не найдено")));

        Set<Event> events = compilation.getEvents();

        if (events.add(event)) {
            compilation.setEvents(events);

            compilationRepository.save(compilation);
        } else {
            throw new EntityNotFoundException(String.format("Событие с id=%d уже есть в подборке с id=%d",
                    eventId, compId));
        }
    }

    @Override
    @Transactional
    public void setPinned(Long compId, Boolean flag) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Подборка с id=%d не найдена", compId)));

        if (flag) {
            compilation.setPinned(true);
        } else {
            compilation.setPinned(false);
        }

        compilationRepository.save(compilation);
    }

    @Override
    public List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        return compilationMapper.compilationDto(
                compilationRepository.findAllByPinned(pinned, pageable).stream().collect(Collectors.toList()));
    }

    private List<Event> validateListEvents(List<Event> events) {
        List<Event> result = new ArrayList<>();

        for (Event event : events) {
            if (eventRepository.findById(event.getId()).isPresent()) {
                result.add(eventRepository.findById(event.getId()).get());
            } else {
                log.info("Событие с id=%d на найдено и удалено из списка", event.getId());
            }
        }

        return result;
    }
}
