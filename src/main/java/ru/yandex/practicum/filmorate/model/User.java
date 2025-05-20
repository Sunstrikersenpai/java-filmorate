package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    @Email(message = "Неверный формат email")
    @NotBlank(message = "email не может быть пустым")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "В логине не может быть пробелов")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
    @Builder.Default
    private Set<Long> friendList = new HashSet<>();

    public void addToFriendList(Long id) {
        friendList.add(id);
    }

    public void removeFromFriendList(Long id) {
        friendList.remove(id);
    }
}