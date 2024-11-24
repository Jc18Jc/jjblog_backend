package org.jj.jjblog.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jj.jjblog.member.dto.MemberJoinDTO;
import org.jj.jjblog.member.service.MemberService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("")
    public Map<String, String> postMember(@RequestBody MemberJoinDTO memberJoinDTO) throws MemberService.MidExistException {
        log.info("memberJoin POST ----------------");
        log.info(memberJoinDTO);
        memberService.join(memberJoinDTO);
        return Map.of("result", "success");
    }

    @GetMapping("")
    public Map<String, String> getMember(HttpServletRequest request) {
        String username = memberService.findId(request);
        return Map.of("mid", username);
    }
}
