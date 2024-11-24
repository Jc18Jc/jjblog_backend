package org.jj.jjblog.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.jj.jjblog.post.domain.PostImage;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PostDTO{
    private long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String description;
    @NotEmpty
    private String category;
    private String writer;
    @JsonFormat(shape =  JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate day;
    private String fileName;
    private boolean featured;
    @NotEmpty
    private String content;
    private PostCardDTO next;
    private PostCardDTO prev;
    public void setNext(PostCardDTO next) {
        this.next=next;
    }
    public void setPrev(PostCardDTO prev) {
        this.prev=prev;
    }
}
