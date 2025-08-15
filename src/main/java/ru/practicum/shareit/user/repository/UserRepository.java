package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.incrementAndGet());
        }
        users.put(user.getId(), user);
        return user;
    }

    public User findById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            return null;
        }
    }

    public Boolean checkUsers(User user) {
        if (users.containsKey(user.getId())) {
            return false;
        }
        return true;
    }

    public User update(User user) {
        if (checkUsers(user)) {
            throw new RuntimeException("Пользователь с айди" + user.getId() + "не найден");
        }
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

    public void deleteById(Long id) {
        users.remove(id);
    }
}
