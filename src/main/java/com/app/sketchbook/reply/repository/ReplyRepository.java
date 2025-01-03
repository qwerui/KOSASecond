// 작업자 : 이하린

package com.app.sketchbook.reply.repository;

import com.app.sketchbook.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {
    List<Reply> findByPostNo(Long no);
}