package org.jj.jjblog.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jj.jjblog.error.CustomException;
import org.jj.jjblog.error.ErrorCode;
import org.jj.jjblog.post.dto.PostDTO;
import org.jj.jjblog.post.service.PostService;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Log4j2
public class PostController {

    private final PostService postService;

    @PutMapping(value = "/featured/{id}")
    public boolean fixFeatured(@PathVariable("id") long id) {
        return postService.fixFeatured(id);
    }

    @GetMapping("/{id}")
    public PostDTO getReadPost(@PathVariable("id") long id) {
        log.info("read id: " + id);
        return postService.searchOne(id);
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> postPosts(@Valid @RequestPart PostDTO postDTO, BindingResult bindingResult, @RequestPart MultipartFile file) {
        log.info(postDTO);
        log.info(file);
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors());
            throw new CustomException(ErrorCode.FAIL_REGISTER);
        }
        postService.register(postDTO, file);
        return Map.of("result", "success");
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> putPosts(@PathVariable("id") long id, @RequestPart PostDTO postDTO, BindingResult bindingResult, @Nullable @RequestPart MultipartFile file) {
        if (bindingResult.hasErrors()) {
            log.info(bindingResult.getAllErrors());
            throw new CustomException(ErrorCode.FAIL_REGISTER);
        }
        postDTO.setId(id);
        postService.modify(postDTO, file);
        return Map.of("result", "success");
    }

    @DeleteMapping(value = "/{id}")
    public Map<String, String> deletePosts(@PathVariable("id") long id) {
        postService.delete(id);
        return Map.of("result", "success");
    }

}
