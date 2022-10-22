package ru.practicum.explorewme.category;

import org.springframework.stereotype.Component;
import ru.practicum.explorewme.category.dto.CategoryDto;
import ru.practicum.explorewme.category.model.Category;

@Component
public class CategoryMapper {
    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();

        if (categoryDto.getId() != null) {
            category.setId(categoryDto.getId());
        }

        category.setName(categoryDto.getName());

        return category;
    }
}
