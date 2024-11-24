package org.jj.jjblog.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.jj.jjblog.post.domain.PostImage;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCardDTO {
    private long id;
    @NotEmpty
    private String title;
    private String description;
    @NotEmpty
    private String writer;
    private String category;
    @JsonFormat(shape =  JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate day;
    private String fileName;
    private boolean featured;
}