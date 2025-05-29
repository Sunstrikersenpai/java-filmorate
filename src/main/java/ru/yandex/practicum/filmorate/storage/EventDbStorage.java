package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
@AllArgsConstructor
public class EventDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final EventRowMapper eventRowMapper;

    public List<Event> getEvent(Long id) {
        String sql = "SELECT * FROM events WHERE user_id = ? LIMIT 10";

        return jdbcTemplate.query(sql,eventRowMapper,id);
    }

    public void addEvent(Event event) {
        String sql = "INSERT INTO events (user_id, event_type, operation, entity_id) VALUES (?,?,?,?)";

        jdbcTemplate.update(
                sql,event.getUser_id(),event.getEvent_type().name(),
                event.getEventOperation().name(), event.getEntity_id()
                );
    }
}
