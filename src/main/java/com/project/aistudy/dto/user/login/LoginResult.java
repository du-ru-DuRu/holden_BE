package com.project.aistudy.dto.user.login;
import com.project.aistudy.entity.Member;
import lombok.Data;

@Data
public class LoginResult {
    private String accessToken;
    private Member member;
}
