package org.jj.jjblog.member.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jj.jjblog.member.domain.Member;
import org.jj.jjblog.member.dto.MemberJoinDTO;
import org.jj.jjblog.member.repository.MemberRepository;
import org.jj.jjblog.util.JWTUtil;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Log4j2
@Service
public class MemberServiceImpl implements MemberService {
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @Override
    public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {
        String mid = memberJoinDTO.getMid();
        boolean exist = memberRepository.existsById(mid);
        if (exist) {
            throw new MidExistException();
        }
        Member member = modelMapper.map(memberJoinDTO, Member.class);
        member.changeMpw(passwordEncoder.encode(memberJoinDTO.getMpw()));
        log.info(member);
        memberRepository.save(member);
    }

    @Override
    public String findId(HttpServletRequest request) {
        return validateAccessToken(request);
    }

    private String validateAccessToken(HttpServletRequest request) {
        String headerStr = request.getHeader("Authorization");
        if (headerStr == null || headerStr.length() < 8) {
            return "anonymous";
        }
        String tokenType = headerStr.substring(0, 6);
        String tokenStr = headerStr.substring(7);

        if (!tokenType.equalsIgnoreCase("Bearer")) {
            return "anonymous";
        }
        try {
            Map<String, Object> values = jwtUtil.validateToken(tokenStr);
            return (String)values.get("mid");
        } catch (MalformedJwtException | SignatureException | ExpiredJwtException malformedJwtException) {
            return "anonymous";
        }
    }
}
