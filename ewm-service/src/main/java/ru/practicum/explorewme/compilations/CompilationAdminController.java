package ru.practicum.explorewme.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.compilations.dto.CompilationDto;
import ru.practicum.explorewme.compilations.dto.NewCompilationDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@Validated
@Slf4j
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationService compilationService;

    //Добавление новой подборки
    @PostMapping
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("POST /admin/compilations");
        log.info("POST /admin/compilations newCompilationDto={}", newCompilationDto);

        return compilationService.create(newCompilationDto);
    }

    //Удаление подборки
    @DeleteMapping(path = "/{compId}")
    public void deleteCompilation(@PathVariable(name = "compId") Long compId) {
        log.info("DELETE /admin/compilations/{}", compId);

        compilationService.deleteById(compId);
    }

    //Удалить событие из подборки
    @DeleteMapping(path = "/{compId}/events/{eventId}")
    public void deleteEvent(@PathVariable(name = "compId") Long compId, @PathVariable(name = "eventId") Long eventId)
            throws EntityNotFoundException {
        log.info("DELETE /admin/compilations/{}/events/{}", compId, eventId);

        compilationService.deleteEvent(compId, eventId);
    }

    //Добавить событие в подборку
    @PatchMapping(path = "/{compId}/events/{eventId}")
    public void addEvent(@PathVariable(name = "compId") Long compId, @PathVariable(name = "eventId") Long eventId) throws EntityNotFoundException {
        log.info("PATCH /admin/compilations/{}/events/{}", compId, eventId);

       compilationService.addEvent(compId, eventId);
    }

    //Закрепить подборку на главной странице
    @PatchMapping(path = "/{compId}/pin")
    public void setPinned(@PathVariable(name = "compId") Long compId) throws EntityNotFoundException {
        log.info("PATCH /admin/compilations/{}/pin", compId);

        compilationService.setPinned(compId, true);
    }

    //Открепить подборку на главной странице
    @DeleteMapping(path = "/{compId}/pin")
    public void unPinned(@PathVariable(name = "compId") Long compId) throws EntityNotFoundException {
        log.info("DELETE /admin/compilations/{}/pin", compId);

        compilationService.setPinned(compId, false);
    }
}
