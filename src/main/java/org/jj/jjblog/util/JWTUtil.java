package org.jj.jjblog.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class JWTUtil {
    @Value("${org.jj.jwt.secret}")
    private String key;
    public String generateToken(Map<String, Object> valueMap, int days) {
        log.info("generateKey..." + key);
        // 헤더
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        // payload
        Map<String, Object> payloads = new HashMap<>();
        payloads.putAll(valueMap);

        int time = (1) * days;
        //JWT 문자열 생성
        String jwtStr = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusDays(time).toInstant()))
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
        return jwtStr;
    }
    public Map<String, Object> validateToken(String token) {
        Map<String , Object> claim = null;
        claim = Jwts.parser()
                .setSigningKey(key.getBytes())
                .parseClaimsJws(token)
                .getBody(); // 이렇게 유효성 검사하면 사인 에러, 유효기간 에러 등 다 잡아주고, 결과는 claim을 반환해줌
        return claim;
    }
}
