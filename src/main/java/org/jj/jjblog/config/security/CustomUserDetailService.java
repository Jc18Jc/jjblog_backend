package org.jj.jjblog.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jj.jjblog.error.CustomException;
import org.jj.jjblog.error.ErrorCode;
import org.jj.jjblog.member.domain.Member;
import org.jj.jjblog.member.dto.MemberDTO;
import org.jj.jjblog.member.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername: " + username);
        Optional<Member> result = memberRepository.findById(username);
        Member member = result.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<SimpleGrantedAuthority> auth = new ArrayList<>();
        if (member.getMid().equals("jc") || member.getMid().equals("jw")) {
            auth.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        auth.add(new SimpleGrantedAuthority("ROLE_USER"));
        MemberDTO memberDTO = new MemberDTO(member.getMid(), member.getMpw(), auth);
        log.info(memberDTO);
        return memberDTO;
    }


}
