package lcy.jwt.domain;

import java.util.Optional;

public interface UserRepository {
    void save(User user);
    boolean existsByUsername(String username);
    Optional<User> findById(Long id);
}
