package com.app.sketchbook.friend.repository;

import com.app.sketchbook.friend.entity.Friend;
import com.app.sketchbook.friend.entity.FriendStatus;
import com.app.sketchbook.user.entity.SketchUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    //작업자 : 한수민
    List<Friend> findByFromAndStatus(SketchUser from, FriendStatus status);
    List<Friend> findByToAndStatus(SketchUser to, FriendStatus status);
    Optional<Friend> findByFromAndToAndStatus(SketchUser from, SketchUser to, FriendStatus status);
    Optional<Friend> findByFromAndToOrFromAndTo(SketchUser from1, SketchUser to1, SketchUser from2, SketchUser to2);
    boolean existsByFromAndToAndStatus(SketchUser from, SketchUser to, FriendStatus status);
    @Modifying
    @Transactional
    @Query("UPDATE Friend f SET f.status = :newStatus WHERE f.to = :user AND f.status = :oldStatus")
    int updateFriendStatus(@Param("user") SketchUser user,
                           @Param("oldStatus") FriendStatus oldStatus,
                           @Param("newStatus") FriendStatus newStatus);
}