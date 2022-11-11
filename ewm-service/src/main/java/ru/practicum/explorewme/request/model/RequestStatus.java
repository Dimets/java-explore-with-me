package ru.practicum.explorewme.request.model;

public enum RequestStatus {
    PENDING,
    CONFIRMED,
    CANCELED,
    REJECTED
    /*
    insert into request_statuses (id, status) values (1, 'PENDING');
    insert into request_statuses (id, status) values (2, 'CONFIRMED');
    insert into request_statuses (id, status) values (3, 'CANCELED');
    insert into request_statuses (id, status) values (4, 'REJECTED');*/
}
