package ru.practicum.explorewme.category;

import ru.practicum.explorewme.category.dto.CategoryDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;

public interface CategoryService {
    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto findById(Long id) throws EntityNotFoundException;

    void deleteById(Long id);
}
