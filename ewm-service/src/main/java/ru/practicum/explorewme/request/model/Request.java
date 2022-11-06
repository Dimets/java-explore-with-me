package ru.practicum.explorewme.request.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewme.event.model.Event;
import ru.practicum.explorewme.request.requeststatus.model.RequestStatus;
import ru.practicum.explorewme.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@Data
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private RequestStatus status;
}
