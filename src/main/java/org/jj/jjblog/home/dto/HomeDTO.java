package org.jj.jjblog.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jj.jjblog.post.dto.PostCardDTO;
import org.jj.jjblog.post.dto.PostDTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeDTO {

    private List<PostCardDTO> featuredList;
    private List<PostCardDTO> ymlList;
}
