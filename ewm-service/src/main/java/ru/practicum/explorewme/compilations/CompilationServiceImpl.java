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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event>  events = newCompilationDto.getEvents().stream()
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
    public CompilationDto findById(Long compId) throws EntityNotFoundException {
        return compilationMapper.toCompilationDto(compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Подборка с id=%d не найдена", compId))));
    }

    @Override
    @Transactional
    public void deleteEvent(Long compId, Long eventId) throws EntityNotFoundException {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Подборка с id=%d не найдена", compId)));

        List<Event> events = compilation.getEvents();

        if (eventRepository.findById(eventId).isPresent()) {
            events.remove(eventRepository.findById(eventId).get());

            compilation.setEvents(validateListEvents(events));

            //return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
            compilationRepository.save(compilation);

        } else {
            log.info("Событие с id=%d на найдено и удалено из списка", eventId);
            //return findById(compId);
        }
    }

    @Override
    @Transactional
    public void addEvent(Long compId, Long eventId) throws EntityNotFoundException {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Подборка с id=%d не найдена", compId)));

        List<Event> events = compilation.getEvents();

        if (eventRepository.findById(eventId).isPresent()) {
            events.add(eventRepository.findById(eventId).get());

            compilation.setEvents(validateListEvents(events));

            //return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
            compilationRepository.save(compilation);
        } else {
            log.info("Событие с id=%d на найдено и удалено из списка", eventId);
            //return findById(compId);
        }
    }

    @Override
    @Transactional
    public void setPinned(Long compId, Boolean flag) throws EntityNotFoundException {
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
            }  else {
                log.info("Событие с id=%d на найдено и удалено из списка", event.getId());
            }
        }

        return result;
    }
}
