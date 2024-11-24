package org.jj.jjblog.util.upload;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jj.jjblog.post.dto.PostImageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RequiredArgsConstructor
@Log4j2
@Component
@Primary
public class S3Uploader implements Uploader {
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    private final AmazonS3Client amazonS3Client;
    private final LocalUploader localUploader;
    @Override
    public PostImageDTO uploadFile(MultipartFile file) {
        log.info("local image save -----------------------");
        PostImageDTO postImageDTO = localUploader.uploadFile(file);
        log.info(postImageDTO);
        File s3File = new File(postImageDTO.getPath());
        String fileName=s3File.getName();
        log.info("s3 image save ----------------------------");
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, s3File).withCannedAcl(CannedAccessControlList.PublicRead));
        postImageDTO.changePath(amazonS3Client.getUrl(bucket, fileName).toString());
        log.info("local image remove ---------------------------");
        localUploader.removeFile(fileName);
        return postImageDTO;
    }

    @Override
    public void removeFile(String fileName) {
        log.info("s3 image remove -------------------------------");
        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, fileName);
        amazonS3Client.deleteObject(deleteObjectRequest);
    }
}
