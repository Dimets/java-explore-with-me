package ru.practicum.explorewme.stat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewme.stat.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final StatsMapper hitMapper;

    @Override
    @Transactional
    public void create(EndpointHitDto endpointHitDto) {
        statsRepository.save(hitMapper.toHit(endpointHitDto));
    }

    @Override
    public List<ViewStats> viewStats(String start, String end, List<String> uris, Boolean unique) {
        if (unique) {
            statsRepository.findByUriIpUnique(uris,
                    LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return statsRepository.findByUri(uris,
                LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}

