package com.project.aistudy.controller.user;

import com.project.aistudy.dto.user.kakao.KaKaoOAuthToken;
import com.project.aistudy.dto.user.kakao.TokenRequestDto;
import com.project.aistudy.dto.user.login.LoginResult;
import com.project.aistudy.service.user.KakaoService;
import com.project.aistudy.utils.baseResponse.BaseResponse;
import com.project.aistudy.utils.baseResponse.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/user/firebase/token")
    public ResponseEntity<BaseResponse<String>> saveToken(@RequestBody TokenRequestDto tokenRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = (Long) authentication.getPrincipal(); // JWT 토큰에서 추출된 사용자 ID
        kakaoService.saveFireBaseToken(memberId, tokenRequest.getToken());
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS, "Token saved successfully"));
    }

}



