package lcy.jwt.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Error 반환 형 통일용 클래스
 */
@Component
public class ErrorResponseHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void send(HttpServletResponse response, HttpStatus status, String code, String message) throws IOException {
        send(response, ErrorResponse.of(status, code, message));
    }

    private void send(HttpServletResponse response, ErrorResponse error) throws IOException {
        response.setStatus(error.status());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getOutputStream(), error);
        response.getOutputStream().flush();
    }
}

