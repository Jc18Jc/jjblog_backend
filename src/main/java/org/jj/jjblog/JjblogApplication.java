package org.jj.jjblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JjblogApplication {

    public static void main(String[] args) {
        SpringApplication.run(JjblogApplication.class, args);
    }

}
