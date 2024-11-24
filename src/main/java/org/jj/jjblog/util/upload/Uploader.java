package org.jj.jjblog.util.upload;

import org.jj.jjblog.post.dto.PostImageDTO;
import org.springframework.web.multipart.MultipartFile;

public interface Uploader {
    PostImageDTO uploadFile(MultipartFile file);
    void removeFile(String fileName);
}
