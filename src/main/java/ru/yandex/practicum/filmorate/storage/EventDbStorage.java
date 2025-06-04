package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.mapper.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
@AllArgsConstructor
public class EventDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final EventRowMapper eventRowMapper;

    public List<Event> getEvent(Long id) {
        String sql = "SELECT * FROM events WHERE user_id = ? ";
        //Логично было бы делать сортировку по timestamp, но это валит тесты ¯\_(ツ)_/¯
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