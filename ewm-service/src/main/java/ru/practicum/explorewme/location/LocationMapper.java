package ru.practicum.explorewme.location;

import org.springframework.stereotype.Component;
import ru.practicum.explorewme.location.dto.LocationDto;
import ru.practicum.explorewme.location.dto.LocationShortDto;
import ru.practicum.explorewme.location.model.Location;

@Component
public class LocationMapper {
    public LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getId(), location.getLat(), location.getLon());
    }

    public LocationShortDto toLocationShortDto(Location location) {
        return new LocationShortDto(location.getLat(), location.getLon());
    }


    public Location toLocation(LocationShortDto locationShortDto) {
        Location location = new Location();
        location.setLat(locationShortDto.getLat());
        location.setLon(locationShortDto.getLon());
        return location;
    }

    public Location toLocation(LocationDto locationDto) {
        Location location = new Location();
        location.setId(locationDto.getId());
        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());
        return location;
    }
}
