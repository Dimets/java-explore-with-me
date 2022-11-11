package ru.practicum.explorewme.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.compilations.dto.CompilationDto;
import ru.practicum.explorewme.compilations.dto.NewCompilationDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
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
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("DELETE /admin/compilations/{}", compId);

        compilationService.deleteById(compId);
    }

    //Удалить событие из подборки
    @DeleteMapping(path = "/{compId}/events/{eventId}")
    public void deleteEvent(@PathVariable Long compId, @PathVariable Long eventId) {
        log.info("DELETE /admin/compilations/{}/events/{}", compId, eventId);

        compilationService.deleteEvent(compId, eventId);
    }

    //Добавить событие в подборку
    @PatchMapping(path = "/{compId}/events/{eventId}")
    public void addEvent(@PathVariable Long compId, @PathVariable Long eventId) {
        log.info("PATCH /admin/compilations/{}/events/{}", compId, eventId);

        compilationService.addEvent(compId, eventId);
    }

    //Закрепить подборку на главной странице
    @PatchMapping(path = "/{compId}/pin")
    public void setPinned(@PathVariable Long compId) {
        log.info("PATCH /admin/compilations/{}/pin", compId);

        compilationService.setPinned(compId, true);
    }

    //Открепить подборку на главной странице
    @DeleteMapping(path = "/{compId}/pin")
    public void unPinned(@PathVariable Long compId) {
        log.info("DELETE /admin/compilations/{}/pin", compId);

        compilationService.setPinned(compId, false);
    }
}
