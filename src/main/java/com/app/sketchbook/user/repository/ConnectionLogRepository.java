package com.app.sketchbook.user.repository;

import com.app.sketchbook.user.entity.ConnectionLog;
import com.app.sketchbook.user.entity.SketchUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

//작업자 : 홍제기
public interface ConnectionLogRepository extends JpaRepository<ConnectionLog, Long> {
    //접속 기록 조회
    Page<ConnectionLog> findAllByUserOrderByConnectedTimeDesc(Pageable pageable, SketchUser user);
}
