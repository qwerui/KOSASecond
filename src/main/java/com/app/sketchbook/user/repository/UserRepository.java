package com.app.sketchbook.user.repository;

import com.app.sketchbook.user.entity.SketchUser;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
//개발 담당 : 김범철
public interface UserRepository extends JpaRepository<SketchUser, Long> {
    SketchUser findByEmailAndSocial(String email, String social);
    SketchUser findByEmail(String email);
    Optional<SketchUser> findByAuthCode(String code);

    List<SketchUser> findByUsernameContainingIgnoreCase(String keyword);
    List<SketchUser> findByEmailContainingIgnoreCase(String keyword);

    @Query("SELECT u FROM SketchUser u WHERE u.username LIKE %:query%")
    Slice<SketchUser> findByUsername(@Param("query") String query, Pageable pageable);

//    @Query("select u from sketchuser u where u.username = (SELECT f.friend FROM Friend f WHERE f.user.id = :userId and f.)")
//    Slice<SketchUser> findByFriend(@Param("query") String query, Pageable pageable);

}