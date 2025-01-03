// 작업자 : 이하린

package com.app.sketchbook.post.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

// 이미지 삭제 리퀘스트 전용 DTO
public class ImageRequestDTO {
    private List<Long> selectedImageIds;
}