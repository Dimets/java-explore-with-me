package ru.practicum.explorewme.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewme.category.dto.CategoryDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;



@Service
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryDto create(CategoryDto categoryDto) {
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(categoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto update(CategoryDto categoryDto) {
        return categoryMapper.toCategoryDto(categoryMapper.toCategory(categoryDto));
    }

    @Override
    public CategoryDto findById(Long id) throws EntityNotFoundException {
        return categoryMapper.toCategoryDto(categoryRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Категория с id=%d не существует", id))));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
