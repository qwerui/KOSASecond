//작업자 : 한수민

package com.app.sketchbook.user.service;

import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;

    //프로필 사진 업로드
    public void uploadProfile(SketchUser user, String fileUrl) {
        user.setProfile_img_url(fileUrl);
        userRepository.save(user);
    }

    //배경 사진 업로드
    public void uploadCover(SketchUser user, String fileUrl) {
        user.setCover_img_url(fileUrl);
        userRepository.save(user);
    }

    //프로필 공개 여부
    public void isProfilePublic(SketchUser user, boolean isPublic) {
        user.setProfile_public(isPublic);
        userRepository.save(user);
    }

}
