package com.project.aistudy.controller.user;

import com.project.aistudy.dto.user.kakao.KaKaoOAuthToken;
import com.project.aistudy.dto.user.login.LoginResult;
import com.project.aistudy.service.user.KakaoService;
import com.project.aistudy.utils.baseResponse.BaseResponse;
import com.project.aistudy.utils.baseResponse.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoController {

    private static final Logger log = LoggerFactory.getLogger(KakaoController.class);
    private final KakaoService kakaoService;

    @GetMapping("/user/kakao/token")
    public ResponseEntity<BaseResponse<LoginResult>> kakaoCallback(@RequestParam String code) {
        KaKaoOAuthToken token = kakaoService.getKakaoToken(code);
        LoginResult result = kakaoService.loginWithAccessToken(token.getAccess_token());

        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS, result));
    }

}



