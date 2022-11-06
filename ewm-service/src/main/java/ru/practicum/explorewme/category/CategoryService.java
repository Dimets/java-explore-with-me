package ru.practicum.explorewme.category;

import ru.practicum.explorewme.category.dto.CategoryDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto findById(Long id) throws EntityNotFoundException;

    List<CategoryDto> findAll(Integer from, Integer size);

    void deleteById(Long id);
}
