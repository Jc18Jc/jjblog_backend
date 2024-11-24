package org.jj.jjblog.post.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "post")
public class PostImage {
    @Id
    private String uuid;
    private String fileName;
    private String path;
    @OneToOne
    private Post post;
}
