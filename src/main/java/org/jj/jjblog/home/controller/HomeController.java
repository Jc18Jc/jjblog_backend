package org.jj.jjblog.home.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jj.jjblog.home.dto.HomeDTO;
import org.jj.jjblog.page.dto.PageRequestDTO;
import org.jj.jjblog.page.dto.PageResponseDTO;
import org.jj.jjblog.post.dto.PostCardDTO;
import org.jj.jjblog.post.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Log4j2
public class HomeController {
    private final PostService postService;

    @GetMapping("/home")
    public HomeDTO getHome() {
        return postService.searchHomeList();
    }

    @GetMapping("/list")
    public PageResponseDTO<PostCardDTO> getPosts(PageRequestDTO pageRequestDTO) {
        log.info(pageRequestDTO);
        return postService.searchWithCategory(pageRequestDTO);
    }
}
