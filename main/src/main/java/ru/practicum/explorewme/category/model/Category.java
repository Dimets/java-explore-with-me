package ru.practicum.explorewme.category.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewme.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "categories", schema = "public")
@Data
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        return id != null && id.equals(((Category) o).getId());
    }
}
