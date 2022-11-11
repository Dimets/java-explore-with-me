package ru.practicum.explorewme.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewme.location.dto.LocationDto;
import ru.practicum.explorewme.location.dto.LocationShortDto;
import ru.practicum.explorewme.location.model.Location;

import javax.persistence.EntityNotFoundException;

@Service
@Slf4j
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {
    private final LocationMapper locationMapper;
    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationMapper locationMapper, LocationRepository locationRepository) {
        this.locationMapper = locationMapper;
        this.locationRepository = locationRepository;
    }

    @Override
    @Transactional
    public LocationDto create(LocationShortDto locationShortDto) {
        return locationMapper.toLocationDto(locationRepository.save(locationMapper.toLocation(locationShortDto)));
    }

    @Override
    @Transactional
    public LocationDto findByCoordinates(Double lat, Double lon) {
        //создаем новую локацию если не находим существующую
        if (locationRepository.findByLatAndLon(lat, lon).isPresent()) {
            return locationMapper.toLocationDto(locationRepository.findByLatAndLon(lat, lon).get());
        } else {
            return locationMapper.toLocationDto(locationRepository.save(new Location(lat, lon)));
        }
    }

    @Override
    public LocationShortDto findById(Long id) {
        return locationMapper.toLocationShortDto(locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Локация с id=%d не найдена", id))));
    }
}
