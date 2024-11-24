package org.jj.jjblog.config.filter;

import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jj.jjblog.error.CustomException;
import org.jj.jjblog.error.ErrorCode;
import org.jj.jjblog.error.ErrorResponseEntity;
import org.jj.jjblog.util.JWTUtil;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Log4j2
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final String refreshPath;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        // CustomSecurityConfig로 필터들이 실행되는데 해당 필터는 /refreshToken uri로 호출된 작업만 실행한다는 뜻, CustomSecurityConfig에 refreshPath 있음
        if (!path.equals(refreshPath)) {
            log.info("skip refresh token filter");
            filterChain.doFilter(request, response);
            return;
        }
        log.info("Refresh Token Filter ... run ..............");

        Map<String, String> tokens = parseRequestJSON(request);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        log.info("accessToken: " + accessToken);
        log.info("refreshToken: " + refreshToken);

        try {
            checkAccessToken(accessToken);
        } catch (CustomException customException) {
            ErrorResponseEntity.sendResponseError(response, customException.getErrorCode());
            return;
        }

        Map<String, Object> refreshClaims = null;

        try {
            refreshClaims = checkRefreshToken(refreshToken);
            log.info(refreshClaims);

            // Refresh Token의 유효기간이 얼마 남지 않은 경우
            Integer exp = (Integer) refreshClaims.get("exp");
            Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
            Date current = new Date(System.currentTimeMillis());
            // 3일 미만일 경우
            long gapTime = (expTime.getTime() - current.getTime());
            log.info("-----------------------------------------------");
            log.info("current: " + current);
            log.info("expTime: " + expTime);
            log.info("gapTime: " + gapTime);

            // accessToken은 항상 신규 발행
            String mid = (String) refreshClaims.get("mid");
            String accessTokenValue = jwtUtil.generateToken(Map.of("mid", mid), 1);

            String refreshTokenValue = tokens.get("refreshToken");
            if (gapTime < (1000 * 60 * 60 * 24 * 3)) {
                log.info("new Refresh Token required ... ");
                refreshTokenValue = jwtUtil.generateToken(Map.of("mid", mid), 30);
            }
            log.info("Refresh Token result .................");
            log.info("accessToken: " + accessTokenValue);
            log.info("RefreshToken: " + refreshTokenValue);

            sendTokens(accessTokenValue, refreshTokenValue, response);

        } catch (CustomException customException) {
            ErrorResponseEntity.sendResponseError(response, customException.getErrorCode());
        }
    }

    private Map<String, String> parseRequestJSON(HttpServletRequest request) {
        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private void checkAccessToken(String accessToken) {
        try {
            jwtUtil.validateToken(accessToken);
        } catch (ExpiredJwtException expiredJwtException) { // 기간 만료라면
            log.info("Access Token has expired");
        } catch (Exception exception) { // 기간 만료가 아닌 예외 발생
            throw new CustomException(ErrorCode.ACCESS_TOKEN_UNACCEPT);
        }
    }

    private Map<String, Object> checkRefreshToken(String refreshToken) {
        try {
            Map<String, Object> values = jwtUtil.validateToken(refreshToken);
            return values;
        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (MalformedJwtException malformedJwtException) {
            log.error("MalformedJwtException---------------------");
            throw new CustomException(ErrorCode.REFRESH_TOKEN_UNACCEPT);
        } catch (Exception exception) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_UNACCEPT);
        }
    }

    private void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(Map.of("accessToken", accessTokenValue, "refreshToken", refreshTokenValue));
        try {
            response.getWriter().println(jsonStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
