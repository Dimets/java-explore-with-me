package ru.practicum.explorewme.location;

import ru.practicum.explorewme.location.dto.LocationDto;
import ru.practicum.explorewme.location.dto.LocationShortDto;

public interface LocationService {
    LocationDto create(LocationShortDto locationShortDto);

    LocationDto findByCoordinates(Double lat, Double lon);

    LocationShortDto findById(Long id);
}
