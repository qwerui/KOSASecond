package com.app.sketchbook.user.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
public class SketchUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String role;

    private String profile_img_url;
//
    private String cover_img_url;

    private String social;
//
    private String address;
//
    private Date birth;
//
    private LocalDateTime join_date;
//
    private String phone;
//
    private String password;

    private String gender;

    private String authCode;

    private Boolean enabled;

    private Boolean update_pw=false;

    private boolean profile_public = true;
}