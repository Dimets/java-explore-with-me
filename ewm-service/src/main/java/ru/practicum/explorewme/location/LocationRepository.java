package ru.practicum.explorewme.location;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewme.location.model.Location;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByLatAndLon(Double lat, Double lon);
}
