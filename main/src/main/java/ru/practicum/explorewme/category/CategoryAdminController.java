package ru.practicum.explorewme.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewme.category.dto.CategoryDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;
import ru.practicum.explorewme.exception.ValidationException;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@Validated
@Slf4j
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("POST /admin/categories");
        log.debug("POST /admin/categories categoryDto={}", categoryDto);

        return categoryService.create(categoryDto);
    }

    @PatchMapping
    public CategoryDto updateCategory(@RequestBody @Valid CategoryDto categoryDto)
            throws EntityNotFoundException, ValidationException {
        log.info("PATCH /admin/categories");
        log.debug("PATCH /admin/categories body={}", categoryDto);

        if (categoryDto.getId() == null) {
            throw new ValidationException("Id is required for update operation");
        }

        categoryService.findById(categoryDto.getId());

        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(@PathVariable("categoryId") Long categoryId) {
        log.info("DELETE /admin/categories/{}", categoryId);
        categoryService.deleteById(categoryId);
    }

}
