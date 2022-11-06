package ru.practicum.explorewme.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.category.dto.CategoryDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@Validated
@Slf4j
@RequiredArgsConstructor
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public CategoryDto findById(@PathVariable("catId") Long catId) throws EntityNotFoundException {
        log.info("GET /categories/{}", catId);
        return categoryService.findById(catId);
    }

    @GetMapping
    public List<CategoryDto> findAll(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET /categories from={}, size={}", from, size);
        return categoryService.findAll(from, size);
    }
}
