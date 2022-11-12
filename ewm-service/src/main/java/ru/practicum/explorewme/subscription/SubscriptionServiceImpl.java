package ru.practicum.explorewme.subscription;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewme.event.EventMapper;
import ru.practicum.explorewme.event.EventRepository;
import ru.practicum.explorewme.event.EventService;
import ru.practicum.explorewme.event.dto.EventShortDto;
import ru.practicum.explorewme.event.model.Event;
import ru.practicum.explorewme.event.model.EventState;
import ru.practicum.explorewme.exception.EntityNotFoundException;
import ru.practicum.explorewme.subscription.dto.SubscriptionDto;
import ru.practicum.explorewme.subscription.model.Subscription;
import ru.practicum.explorewme.subscription.model.SubscriptionStatus;
import ru.practicum.explorewme.user.UserMapper;
import ru.practicum.explorewme.user.UserRepository;
import ru.practicum.explorewme.user.dto.UserShortDto;
import ru.practicum.explorewme.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Transactional
    @Override
    public SubscriptionDto create(Long subsId, Long userId) {
        User subscriber = userRepository.findById(subsId)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Пользователь с id=%d не существует", subsId))
                );

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Пользователь с id=%d не существует", userId))
                );


        Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        subscription.setUser(user);
        subscription.setStatus(SubscriptionStatus.CONFIRMED);

        return subscriptionMapper.toSubscriptionDto(subscriptionRepository.save(subscription));
    }

    @Override
    public List<UserShortDto> findAllUsers(Long subsId) {

        if (userRepository.existsById(subsId)) {
            return userMapper.toUserShortDto(List.copyOf(userRepository.findById(subsId).get().getSubscriptions()));
        }

        throw new EntityNotFoundException(String.format("Пользователь с id=%d не существует", subsId));
    }

    @Override
    public List<EventShortDto> findAllEvents(Long subsId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<UserShortDto> userShortDtoList = findAllUsers(subsId);

        List<EventShortDto> eventShortDtoList = new ArrayList<>();

        for (UserShortDto e : userShortDtoList) {
            eventShortDtoList.addAll(eventMapper.toEventShortDto(eventRepository.findByInitiatorIdAndStateIn(e.getId(),
                    List.of(EventState.PUBLISHED), pageable).stream().collect(Collectors.toList())));
        }

        return eventShortDtoList;
    }
}
