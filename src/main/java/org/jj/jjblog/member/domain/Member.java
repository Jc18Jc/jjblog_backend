package org.jj.jjblog.member.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member {
    @Id
    private String mid;
    private String mpw;

    public void changeMpw(String mpw) {
        this.mpw = mpw;
    }
}
