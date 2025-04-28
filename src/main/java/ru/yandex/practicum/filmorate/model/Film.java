package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Описание не может быть больше 200 символов")
    private String description;
    @ReleaseDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть больше 0")
    private Integer duration;
    @Builder.Default
    private Set<Long> usersLikes = new HashSet<>();
}
