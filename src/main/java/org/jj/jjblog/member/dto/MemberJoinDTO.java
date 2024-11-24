package org.jj.jjblog.member.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class MemberJoinDTO {
    private String mid;
    private String mpw;
}
