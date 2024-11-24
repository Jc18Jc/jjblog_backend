package org.jj.jjblog.page.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page=1;
    @Builder.Default
    private int size=9;
    private String category;

    public Pageable getPageable(String...props) {
        return PageRequest.of(this.page-1, this.size, Sort.by(props).descending());
    }

    private String link;

    public String getLink() {
        if (link==null) {
            StringBuilder builder = new StringBuilder();
            builder.append("page=" + this.page);
            builder.append("&size=" + this.size);
            if (category!=null) {
                builder.append("&category=" + category);
            }
            link = builder.toString();
        }
        return link;
    }
}
