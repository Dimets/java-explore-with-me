package ru.practicum.explorewme.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewme.category.model.Category;
import ru.practicum.explorewme.event.eventstate.model.EventState;
import ru.practicum.explorewme.location.model.Location;
import ru.practicum.explorewme.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", schema = "public")
@Data
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "is_paid")
    private Boolean paid;

    @Column(name = "participiant_limit")
    private int participantLimit;

    @Column(name = "published_date")
    private LocalDateTime publishedOn;

    @Column(name = "is_moderated")
    private Boolean requestModeration;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private EventState state;

    private String title;

    @Column(name = "create_date")
    private LocalDateTime createdOn = LocalDateTime.now();

}
