package ru.yandex.practicum.filmorate.storage.event;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.mapper.EventRowMapper;

import java.util.List;

@Repository
@AllArgsConstructor
@Qualifier("eventDbStorage")
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;
    private final EventRowMapper eventRowMapper;

    public List<Event> getEvent(Long id) {
        String sql = "SELECT * FROM events WHERE user_id = ? ";
        return jdbcTemplate.query(sql, eventRowMapper, id);
    }

    public void addEvent(Event event) {
        String sql = "INSERT INTO events (user_id, event_type, operation, entity_id) VALUES (?,?,?,?)";

        jdbcTemplate.update(
                sql, event.getUserId(), event.getEventType().name(),
                event.getOperation().name(), event.getEntityId()
        );
    }
}