package ru.practicum.explorewme.category;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewme.category.dto.CategoryDto;
import ru.practicum.explorewme.exception.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

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

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        return categoryMapper.toCategoryDto(categoryRepository.findAll(pageable).stream().collect(Collectors.toList()));
    }
}
