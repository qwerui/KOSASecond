package com.app.sketchbook;

import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Log4j2
@SpringBootTest
class SketchBookApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        SketchUser sketchUser = new SketchUser();
        sketchUser.setUsername("TEST");
        sketchUser.setPassword("test");
        sketchUser.setRole("ROLE_ADMIN");
        this.userRepository.save(sketchUser);
    }


    @Test
    void testSearch(){
        String query ="김범철";
        PageRequest pageRequest = PageRequest.of(1, 3);
        Slice<SketchUser>user = userRepository.findByUsername(query,pageRequest);
        log.info(user);

    }

    @Test
    public void test1() {
        SketchUser user = userRepository.getReferenceById(1L);
        String query ="김범철";
        PageRequest pageRequest = PageRequest.of(0, 3); // 0페이지에서 3개
        Slice<SketchUser> slice = userRepository.findByUsername(query,pageRequest);
        List<SketchUser> list = slice.getContent();

        System.out.println(list);
    }
}
