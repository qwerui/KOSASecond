package com.app.sketchbook.post.repository;

import com.app.sketchbook.post.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}