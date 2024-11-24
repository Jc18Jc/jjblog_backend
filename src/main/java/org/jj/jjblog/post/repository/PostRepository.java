package org.jj.jjblog.post.repository;

import org.jj.jjblog.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "select p from Post p where p.featured=true order by p.id desc")
    List<Post> findByIdWithFeatured();
    @Query(value = "select p from Post p where p.featured=false order by p.id desc")
    List<Post> findByIdWithYml();
    @Query(value = "select p from Post p order by p.id desc")
    Page<Post> findAllPost(Pageable pageable);
    @Query(value = "select p from Post p where p.category=:category order by p.id desc")
    Page<Post> findWithCategory(@Param("category") String category, Pageable pageable);
    @Query(value = "select count(p) from Post p where p.featured=true")
    int countFeatured();
    @Query(value= "select p from Post p where p.id>:id order by p.id asc limit 1")
    Optional<Post> findNext(@Param("id") long id);
    @Query(value= "select p from Post p where p.id<:id order by p.id desc limit 1")
    Optional<Post> findPrev(@Param("id") long id);

}
