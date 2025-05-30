package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("userId")
    private Long user_id;
    @JsonProperty("eventType")
    private EventType event_type;
    @JsonProperty("operation")
    private EventOperation eventOperation;
    @JsonProperty("entityId")
    private Long entity_id;
    private Long timestamp;
}


