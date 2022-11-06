package ru.practicum.explorewme.request.requeststatus.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "request_statuses", schema = "public")
@Data
public class RequestStatus {
    @Id
    private Long id;

    private String status;
}
