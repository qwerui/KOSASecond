package com.app.sketchbook.user.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;

@Getter
@Setter
@ToString
public class ModifyUserForm {
    private String name;
    private String tel;
    private String address;
    private Date birth;
    private String gender;
}
