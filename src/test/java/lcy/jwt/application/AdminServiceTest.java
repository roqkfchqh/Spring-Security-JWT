package lcy.jwt.application;

import lcy.jwt.domain.User;
import lcy.jwt.domain.UserRepository;
import lcy.jwt.domain.UserRole;
import lcy.jwt.dto.UserResponse;
import lcy.jwt.exception.CustomException;
import lcy.jwt.mocks.MockUserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    private User user;

    @BeforeEach
    void setUp() {
        user = MockUserFactory.createUser(10L);
    }

    @Test
    void assignAdminRole_성공() {
        given(userRepository.findByUserId(10L)).willReturn(Optional.of(user));

        UserResponse response = adminService.assignAdminRole(10L);

        assertEquals(10L, response.id());
        assertEquals(UserRole.ADMIN.toString(), response.role());
        verify(userRepository).save(user);
    }

    @Test
    void assignAdminRole_해당_유저가_존재하지_않을때_예외() {
        given(userRepository.findByUserId(99L)).willReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> adminService.assignAdminRole(99L));
        assertEquals("us404", ex.getCode());
    }

    @Test
    void assignAdminRole_이미_ADMIN일떄_예외() {
        User admin = MockUserFactory.createAdmin(20L);
        given(userRepository.findByUserId(20L)).willReturn(Optional.of(admin));

        CustomException ex = assertThrows(CustomException.class,
                () -> adminService.assignAdminRole(20L));
        assertEquals("ad400", ex.getCode());
    }
}
