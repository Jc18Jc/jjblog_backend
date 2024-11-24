package org.jj.jjblog.post.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostImageDTO {
    private String uuid;
    private String fileName;
    private String path;
    public void changePath(String path) {
        this.path = path;
    }
}
