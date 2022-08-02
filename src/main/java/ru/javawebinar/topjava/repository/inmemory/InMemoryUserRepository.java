package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryUserRepository implements UserRepository {
    //private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);

    private final Map<Integer, User> usersMap = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public boolean delete(int id) {
        return usersMap.remove(id) != null;
    }

    @Override
    public User save(User user) {
        if(user.isNew()) {
            user.setId(counter.incrementAndGet());
            usersMap.put(user.getId(), user);
            return user;
        }
        return usersMap.computeIfPresent(user.getId(), (id, oldUser) -> user);
    }

    @Override
    public User get(int id) {
        return usersMap.get(id);
    }

    @Override
    public List<User> getAll() {
        return (List<User>) usersMap.values();
    }

    @Override
    public User getByEmail(String email) {
        return getAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny()
                .orElse(null);
    }
}
