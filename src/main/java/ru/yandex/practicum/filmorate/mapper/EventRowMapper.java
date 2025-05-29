package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setId(rs.getLong("event_id"));
        event.setUser_id(rs.getLong("user_id"));
        event.setEvent_type(EventType.valueOf(rs.getString("event_type")));
        event.setEventOperation(EventOperation.valueOf(rs.getString("operation")));
        event.setEntity_id(rs.getLong("entity_id"));
        event.setTimestamp(rs.getTimestamp("timestamp").toInstant().toEpochMilli());

        return event;
    }
}
