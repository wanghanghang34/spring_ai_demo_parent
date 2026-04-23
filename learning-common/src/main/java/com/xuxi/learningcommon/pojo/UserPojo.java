package com.xuxi.learningcommon.pojo;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserPojo {
    
    private Long id;
    
    private String name;
    
    private Integer age;
    
    private String email;
    
    private String phone;
    
    private String gender;
    
    private LocalDate birthday;
    
    private String address;
    
}
