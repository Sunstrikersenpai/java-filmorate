package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long id;
    private Long user_id;
    private EventType event_type;
    private EventOperation eventOperation;
    private Long entity_id;
    private Long timestamp;
}
