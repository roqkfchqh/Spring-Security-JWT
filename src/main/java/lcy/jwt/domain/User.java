package lcy.jwt.domain;

import lombok.Data;

@Data
public class User {
    Long id;
    String username;
    String nickname;
    String password;
    UserRole role;

    public static User of(String username, String nickname, String password, UserRole role) {
        User user = new User();
        user.username = username;
        user.nickname = nickname;
        user.password = password;
        user.role = role;
        return user;
    }
}
