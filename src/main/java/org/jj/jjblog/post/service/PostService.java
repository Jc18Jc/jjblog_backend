package org.jj.jjblog.post.service;

import org.jj.jjblog.home.dto.HomeDTO;
import org.jj.jjblog.page.dto.PageRequestDTO;
import org.jj.jjblog.page.dto.PageResponseDTO;
import org.jj.jjblog.post.dto.PostCardDTO;
import org.jj.jjblog.post.dto.PostDTO;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    HomeDTO searchHomeList();
    long register(PostDTO postDTO, MultipartFile file);
    PageResponseDTO<PostCardDTO> searchAll(PageRequestDTO pageRequestDTO);
    PostDTO searchOne(long id);
    PageResponseDTO<PostCardDTO> searchWithCategory(PageRequestDTO pageRequestDTO);
    boolean fixFeatured(long id);
    void modify(PostDTO postDTO, MultipartFile file);
    void delete(long id);
}
