package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private final AtomicLong idGenerator = new AtomicLong();


    public User save(User user) {

        if (user.getId() == null) {
            user.setId(idGenerator.incrementAndGet());
        }
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    public User findById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return user;
    }

    public Boolean checkUsers(User user) {
        if (users.containsKey(user.getId())) {
            return false;
        }
        return true;
    }

    public User update(User user) {
        users.remove(user.getId());
        save(user);
        return user;
    }


    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Set<String> getEmails() {
        return new HashSet<>(emails); // возвращаем копию, чтобы нельзя было изменить напрямую
    }

    public void deleteById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        User user = users.remove(id);
        emails.remove(user.getEmail());
    }
}
