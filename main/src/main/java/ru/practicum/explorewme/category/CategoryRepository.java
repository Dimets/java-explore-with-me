package ru.practicum.explorewme.category;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewme.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
