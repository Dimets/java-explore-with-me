package ru.practicum.explorewme.eventstate.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "event_states", schema = "public")
@Data
public class EventState {
    @Id
    private Long id;

    private String state;
}
