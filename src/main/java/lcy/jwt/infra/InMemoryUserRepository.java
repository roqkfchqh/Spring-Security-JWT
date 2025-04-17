package lcy.jwt.infra;

import lcy.jwt.domain.User;
import lcy.jwt.domain.UserRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final ConcurrentMap<Long, User> storeById = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, User> storeByName = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public void save(User user) {
        if (user.getId() == null) {
            user.setId(idGen.getAndIncrement());
        }
        storeById.put(user.getId(), user);
        storeByName.put(user.getUsername(), user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return storeByName.containsKey(username);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(storeByName.get(username));
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
        return Optional.ofNullable(storeById.get(userId));
    }
}
