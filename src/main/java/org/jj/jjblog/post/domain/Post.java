package org.jj.jjblog.post.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;
    @Column(length = 500, nullable = false)
    private String title;
    @Column(length = 500, nullable = false)
    private String description;
    @Column(length = 5000, nullable = false)
    private String content;
    private String writer;
    private String category;
    @OneToOne(mappedBy = "post", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    //다른 어노테이션 생략했는데 문제있으면 고려
    private PostImage image;
    @ColumnDefault("false")
    private boolean featured;

    public void change(String title, String description, String content, String category) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.category = category;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public void setImage(PostImage image) {
        this.image = image;
    }

    public String getImageName() {
        return image.getUuid()+"_"+image.getFileName();
    }

    public String getFilePath() {
        return image.getPath();
    }
}
