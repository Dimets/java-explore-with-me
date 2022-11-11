package ru.practicum.explorewme.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.compilations.dto.CompilationDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@Slf4j
@RequiredArgsConstructor
public class CompilationPublicController {
    private final CompilationService compilationService;

    //Получение подборок событий
    @GetMapping
    public List<CompilationDto> findAllCompilations(
            @RequestParam (required = false) Boolean pinned,
            @PositiveOrZero  @RequestParam(defaultValue = "0") Integer from,
            @Positive  @RequestParam(defaultValue = "100") Integer size) {

        log.info("GET /compilations pinned={} from={}, size={}", pinned, from, size);

        return compilationService.findCompilations(pinned, from, size);
    }

    //Получение подборки событий по его id
    @GetMapping("/{compId}")
    public CompilationDto findCompilation(@PathVariable Long compId) {
        log.info("GET /compilations/{}", compId);

        return compilationService.findById(compId);
    }
}
