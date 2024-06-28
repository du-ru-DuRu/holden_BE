package com.project.aistudy.dto.user.kakao;

import lombok.Data;

@Data
public class KaKaoOAuthToken {
    private String token_type;
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private Integer refresh_token_expires_in;
    private String scope;
}

