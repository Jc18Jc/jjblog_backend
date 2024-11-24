package org.jj.jjblog.config.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailService;
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (!path.startsWith("/api/posts") && !path.startsWith("/api/member")) {
            filterChain.doFilter(request, response);
            return;
        }
        // securityconfig에서 requestmacher로 잡을 시 cors 문제가 발생해서 수동으로 처리
        if (path.startsWith("/api/posts") && request.getMethod().equals("GET")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (path.startsWith("/api/member") && request.getMethod().equals("GET")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Token Check Filter ---------------------");
        log.info("JWTUtil: " + jwtUtil);

        try {
            Map<String, Object> payload = validateAccessToken(request);

            // 해당 부분은 JWT의 아이디(mid)를 이용해 SecurityContextHolder를 쓰는 과정 p872, JWT만 쓸 때는 생략해도 되는 부분(@PreAuthorize 이용 불가)
            // 둘 다 쓰는 것은 호출마다 APIUserDetailService를 호출해야하고 데베를 계속 쓰게 됨, 비효율적이라 실무에서는 안쓰는 듯..
            String mid = (String) payload.get("mid");
            log.info("mid: " + mid);
            UserDetails userDetails = userDetailService.loadUserByUsername(mid);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (CustomException customException) {
            ErrorResponseEntity.sendResponseError(response, customException.getErrorCode());
        }
    }

    private Map<String, Object> validateAccessToken(HttpServletRequest request) {
        String headerStr = request.getHeader("Authorization");
        //String headerStr = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MTI2NzEyOTUsIm1pZCI6ImpjIiwiaWF0IjoxNzEyNTg0ODk1fQ.gJ-yq-Wl7k0G6quMk_N7nTdAFn9AOlbwaFdD476kvnM";
        log.info(headerStr);
        if (headerStr == null || headerStr.length() < 8) {
            log.error(ErrorCode.ACCESS_TOKEN_UNACCEPT.getMessage());
            throw new CustomException(ErrorCode.ACCESS_TOKEN_UNACCEPT);
        }
        String tokenType = headerStr.substring(0, 6);
        String tokenStr = headerStr.substring(7);

        if (!tokenType.equalsIgnoreCase("Bearer")) {
            throw new CustomException(ErrorCode.ACCESS_TOKEN_BADTYPE);
        }
        try {
            Map<String, Object> values = jwtUtil.validateToken(tokenStr);
            return values;
        } catch (MalformedJwtException malformedJwtException) {
            log.error("MalformedJwtException ---------------------");
            throw new CustomException(ErrorCode.ACCESS_TOKEN_MALFORM);
        } catch (SignatureException signatureException) {
            log.error("SignatureException ---------------------");
            throw new CustomException(ErrorCode.ACCESS_TOKEN_BADSIGN);
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("ExpiredJwtException ---------------------");
            throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        }
    }
}
