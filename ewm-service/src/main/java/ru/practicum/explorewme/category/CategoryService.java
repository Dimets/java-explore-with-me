package ru.practicum.explorewme.category;

import ru.practicum.explorewme.category.dto.CategoryDto;
import ru.practicum.explorewme.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto findById(Long id);

    List<CategoryDto> findAll(Integer from, Integer size);

    void deleteById(Long id);
}
