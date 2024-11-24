package org.jj.jjblog.member.service;

import jakarta.servlet.http.HttpServletRequest;
import org.jj.jjblog.member.dto.MemberJoinDTO;

public interface MemberService {
    class MidExistException extends Exception {

    }
    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;
    String findId(HttpServletRequest request);
}
