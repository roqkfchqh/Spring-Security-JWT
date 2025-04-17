package lcy.jwt.domain;

import lombok.Data;

@Data
public class User {
    Long id;
    String email;
    String password;
    UserRole role;
}
