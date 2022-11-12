package ru.practicum.explorewme.event.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explorewme.category.model.Category;
import ru.practicum.explorewme.location.model.Location;
import ru.practicum.explorewme.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "is_paid", nullable = false)
    private Boolean paid;

    @Column(name = "participiant_limit", nullable = false)
    private int participantLimit;

    @Column(name = "published_date")
    private LocalDateTime publishedOn;

    @Column(name = "is_moderated", nullable = false)
    private Boolean requestModeration;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private EventState state;

    @Column(nullable = false)
    private String title;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

}
