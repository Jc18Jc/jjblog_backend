package org.jj.jjblog.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jj.jjblog.error.CustomException;
import org.jj.jjblog.error.ErrorCode;
import org.jj.jjblog.home.dto.HomeDTO;
import org.jj.jjblog.page.dto.PageRequestDTO;
import org.jj.jjblog.page.dto.PageResponseDTO;
import org.jj.jjblog.post.domain.Post;
import org.jj.jjblog.post.domain.PostImage;
import org.jj.jjblog.post.dto.PostCardDTO;
import org.jj.jjblog.post.dto.PostDTO;
import org.jj.jjblog.post.dto.PostImageDTO;
import org.jj.jjblog.post.repository.PostRepository;
import org.jj.jjblog.util.upload.Uploader;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final Uploader uploader;
    
    @Override
    public HomeDTO searchHomeList() {
        List<Post> listFeatured = postRepository.findByIdWithFeatured();
        log.info("--listFeatured--");
        log.info(listFeatured);
        List<PostCardDTO> listFeaturedDTO = listFeatured.stream().map(post ->  {
            PostCardDTO postCardDTO = modelMapper.map(post, PostCardDTO.class);
            postCardDTO.setFileName(post.getFilePath());
            return postCardDTO;
        }).collect(Collectors.toList());
        List<Post> listYml = postRepository.findByIdWithYml();
        log.info("--listYml--");
        log.info(listYml);
        List<PostCardDTO> listYmlDTO = listYml.stream().map(post -> {
            PostCardDTO postCardDTO = modelMapper.map(post, PostCardDTO.class);
            postCardDTO.setFileName(post.getFilePath());
            return postCardDTO;
        }).collect(Collectors.toList());
        log.info("--listFeaturedDTO--");
        log.info(listFeaturedDTO);
        log.info("--listYmlDTO--");
        log.info(listYmlDTO);
        return HomeDTO.builder()
                .featuredList(listFeaturedDTO)
                .ymlList(listYmlDTO)
                .build();
    }

    @Override
    public long register(PostDTO postDTO, MultipartFile file) {
        Post post = modelMapper.map(postDTO, Post.class);
        PostImageDTO postImageDTO = uploader.uploadFile(file);
        PostImage postImage = PostImage.builder()
                .post(post)
                .fileName(postImageDTO.getFileName())
                .uuid(postImageDTO.getUuid())
                .path(postImageDTO.getPath())
                .build();
        post.setImage(postImage);
        log.info(post);
        return postRepository.save(post).getId();
    }

    @Override
    public PageResponseDTO<PostCardDTO> searchAll(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("id");
        Page<Post> result = postRepository.findAllPost(pageable);
        List<PostCardDTO> postCardDTOS =result.getContent().stream().map(post -> {
            PostCardDTO postCardDTO = modelMapper.map(post, PostCardDTO.class);
            postCardDTO.setFileName(post.getFilePath());
            return postCardDTO;
        }).collect(Collectors.toList());
        log.info("searchAll Page<Post>");
        log.info(result);
        return PageResponseDTO.<PostCardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .total((int)result.getTotalElements())
                .postCard(postCardDTOS)
                .build();
    }

    @Override
    public PostDTO searchOne(long id) {
        Optional<Post> result = postRepository.findById(id);
        Post post = result.orElseThrow();
        PostDTO postDTO = modelMapper.map(post, PostDTO.class);
        postDTO.setFileName(post.getFilePath());
        log.info("postDTO: " + postDTO);
        Optional<Post> resultNext = postRepository.findNext(id);
        Post next = resultNext.orElse(null);
        if (next != null) { // if문 없이 postDTO.setNext(modelMapper.map(next, PostCardDTO.class)); 입력하면 modelMapper에서 PostCard의 NotNull이 에러
            PostCardDTO postCardDTO = modelMapper.map(next, PostCardDTO.class);
            postCardDTO.setFileName(next.getFilePath());
            postDTO.setNext(postCardDTO);
        } else {
            postDTO.setNext(null);
        }
        Optional<Post> resultPrev = postRepository.findPrev(id);
        Post prev = resultPrev.orElse(null);
        if (prev != null) {
            PostCardDTO postCardDTO = modelMapper.map(prev, PostCardDTO.class);
            postCardDTO.setFileName(prev.getFilePath());
            postDTO.setPrev(postCardDTO);
        } else {
            postDTO.setPrev(null);
        }
        log.info("postDTO: " + postDTO);
        return postDTO;
    }

    @Override
    public PageResponseDTO<PostCardDTO> searchWithCategory(PageRequestDTO pageRequestDTO) {
        String category = pageRequestDTO.getCategory();
        if (category == null)
            return searchAll(pageRequestDTO);
        Pageable pageable = pageRequestDTO.getPageable("id");
        Page<Post> result = postRepository.findWithCategory(category, pageable);
        List<PostCardDTO> postCardDTOS =result.getContent().stream().map(post -> {
            PostCardDTO postCardDTO = modelMapper.map(post, PostCardDTO.class);
            postCardDTO.setFileName(post.getFilePath());
            return postCardDTO;
        }).collect(Collectors.toList());
        log.info("searchWithCategory Page<Post>");
        log.info(result);
        return PageResponseDTO.<PostCardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .total((int)result.getTotalElements())
                .postCard(postCardDTOS)
                .build();
    }

    @Override
    public boolean fixFeatured(long id) {
        Optional<Post> result = postRepository.findById(id);
        Post post = result.orElseThrow();
        boolean check = !post.isFeatured();
        post.setFeatured(check);
        if (check) {
            int count = postRepository.countFeatured();
            if (count >= 8) {
                throw new CustomException(ErrorCode.OVER_MAX_PIN);
            }
        }
        postRepository.save(post);
        return check;
    }

    @Override
    public void modify(PostDTO postDTO, MultipartFile file) {
        Optional<Post> result = postRepository.findById(postDTO.getId());
        Post post = result.orElseThrow();
        post.change(postDTO.getTitle(), postDTO.getDescription(), postDTO.getContent(), postDTO.getCategory());
        if (!(file==null)) {
            uploader.removeFile(post.getImageName());
            PostImageDTO postImageDTO = uploader.uploadFile(file);
            PostImage postImage = PostImage.builder()
                    .post(post)
                    .fileName(postImageDTO.getFileName())
                    .uuid(postImageDTO.getUuid())
                    .path(postImageDTO.getPath())
                    .build();
            post.setImage(postImage);
        }
        postRepository.save(post);
    }

    @Override
    public void delete(long id) {
        Optional<Post> result = postRepository.findById(id);
        Post post = result.orElseThrow();
        uploader.removeFile(post.getImageName());
        postRepository.deleteById(id);
    }
}
