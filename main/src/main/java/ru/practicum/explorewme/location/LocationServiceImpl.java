package ru.practicum.explorewme.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewme.location.dto.LocationDto;
import ru.practicum.explorewme.location.dto.LocationShortDto;

import javax.persistence.EntityNotFoundException;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final LocationMapper locationMapper;
    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationMapper locationMapper, LocationRepository locationRepository) {
        this.locationMapper = locationMapper;
        this.locationRepository = locationRepository;
    }

    @Override
    public LocationDto create(LocationShortDto locationShortDto) {
        return locationMapper.toLocationDto(locationRepository.save(locationMapper.toLocation(locationShortDto)));
    }

    @Override
    public LocationDto findByCoordinates(Double lat, Double lon) {
        log.info("findByCoordinates");
        return locationMapper.toLocationDto(locationRepository.findByLatAndLon(lat, lon)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Локация с координатами lat=%d lon=%d не найдена", lat, lon))));
    }

    @Override
    public LocationShortDto findById(Long id) {
        return locationMapper.toLocationShortDto(locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Локация с id=%d не найдена", id))));
    }
}
