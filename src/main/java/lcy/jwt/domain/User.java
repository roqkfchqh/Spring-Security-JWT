package lcy.jwt.domain;

import lombok.Data;

@Data
public class User {
    Long id;
    String username;
    String nickname;
    String password;
    UserRole role;
}
