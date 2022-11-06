package ru.practicum.explorewme.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.compilations.dto.CompilationDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@Validated
@Slf4j
@RequiredArgsConstructor
public class CompilationPublicController {
    private final CompilationService compilationService;

    //Получение подборок событий
    @GetMapping
    public List<CompilationDto> findAllCompilations(
            @RequestParam (name = "pinned", required = false) Boolean pinned,
            @PositiveOrZero  @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive  @RequestParam(name = "size", defaultValue = "100") Integer size) {

        log.info("GET /compilations pinned={} from={}, size={}", pinned, from, size);

        return compilationService.findCompilations(pinned, from, size);
    }

    //Получение подборки событий по его id
    @GetMapping("/{compId}")
    public CompilationDto findCompilation(@PathVariable(name = "compId") Long compId) throws EntityNotFoundException {
        log.info("GET /compilations/{}", compId);

        return compilationService.findById(compId);
    }
}
