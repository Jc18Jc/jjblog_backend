package org.jj.jjblog.util.upload;

import lombok.extern.log4j.Log4j2;
import org.jj.jjblog.error.CustomException;
import org.jj.jjblog.error.ErrorCode;
import org.jj.jjblog.post.dto.PostImageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Log4j2
@Component
public class LocalUploader implements Uploader {

    @Value("${org.jj.upload.local.path}")
    private String uploadPath;
    @Override
    public PostImageDTO uploadFile(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        log.info(originalName);
        String uuid = UUID.randomUUID().toString();
        Path savePath = Paths.get(uploadPath, uuid + "_" + originalName);
        try {
            file.transferTo(savePath);
            File tmpFile = new File(uploadPath, uuid + "_" + originalName);
            return PostImageDTO.builder()
                    .uuid(uuid)
                    .fileName(originalName)
                    .path(tmpFile.getAbsolutePath())
                    .build();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_BAD);
        }
    }

    @Override
    public void removeFile(String fileName) {
        Path path = Paths.get(uploadPath, fileName);
        Resource resource = new FileSystemResource(path);
        try {
            resource.getFile().delete();
        } catch (IOException e) {
            //throw new CustomException(ErrorCode.FILE_NOT_DELETE);
        }
    }
}
