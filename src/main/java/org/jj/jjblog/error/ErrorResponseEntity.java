package org.jj.jjblog.error;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.lang.constant.Constable;
import java.util.Map;

@Data
@Builder
@Log4j2
public class ErrorResponseEntity {
    private int status;
    private String name;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e) {
        log.info(e.getMessage());
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .status(e.getHttpStatus().value())
                        .name(e.name())
                        .code(e.getCode())
                        .message(e.getMessage())
                        .build());
    }

    // ControllerAdvice는 filter와 interceptor 단의 예외 처리가 안되기 때문에 직접 전송
    public static void sendResponseError(HttpServletResponse response, ErrorCode e) {
        response.setStatus(e.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Gson gson = new Gson();
        Map<String, ? extends Constable> map = Map.of("status", e.getHttpStatus().value(), "name", e, "code", e.getCode(), "message", e.getMessage());
        String responseStr = gson.toJson(map);
        try {
            response.getWriter().println(responseStr);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
