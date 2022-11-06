package ru.practicum.explorewme.compilations.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewme.event.model.Event;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilations", schema = "public")
@Data
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_pinned")
    private Boolean pinned;

    private String title;

    @ManyToMany
    @JoinTable(
            name = "events_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"), inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> events;
}
