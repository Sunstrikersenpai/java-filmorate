package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.util.List;

@AllArgsConstructor
@Service
public class EventService {

    private EventStorage eventStorage;

    public List<Event> getFeed(Long id) {
        return eventStorage.getEvent(id);
    }

    public void logEvent(Long userId, Long entityId, EventType type, EventOperation operation) {
        eventStorage.addEvent(
                Event.builder().userId(userId).entityId(entityId)
                        .eventType(type).operation(operation).build()
        );
    }
}
