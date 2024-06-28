package com.project.aistudy.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.aistudy.dto.user.kakao.KaKaoOAuthToken;
import com.project.aistudy.dto.user.kakao.OAuthProfile;
import com.project.aistudy.dto.user.login.LoginResult;
import com.project.aistudy.entity.Member;
import com.project.aistudy.repository.user.MemberRepository;
import com.project.aistudy.utils.baseResponse.BaseException;
import com.project.aistudy.utils.baseResponse.BaseResponseStatus;
import com.project.aistudy.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {

    private final RestTemplate restTemplate;
    private final MemberRepository kakaoRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    @Value("${kakao.scope}")
    private String scope;

    //카카오인가 토큰으로 카톡 액세스토큰받기
    public KaKaoOAuthToken getKakaoToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", clientId);
            params.add("redirect_uri", redirectUri);
            params.add("code", code);
            params.add("scope", scope);
            HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );

            ObjectMapper objectMapper = new ObjectMapper();
            KaKaoOAuthToken kaKaoOAuthToken = objectMapper.readValue(response.getBody(), KaKaoOAuthToken.class);
            return kaKaoOAuthToken;
        } catch (JsonProcessingException e) {
            throw new BaseException(BaseResponseStatus.GET_OAUTH_TOKEN_FAILED);
        }
    }

    //카카오액세스토큰으로 로그인하기
    public LoginResult loginWithAccessToken(String accessToken) {
        OAuthProfile profile = getUserProfile(accessToken);
        Optional<Member> member = kakaoRepository.findByKakaoId(profile.getId());

        if (member.isEmpty()) {
            member = Optional.of(signUp(profile));
        }

        String token = jwtTokenGenerator(member.get().getMemberId(), member.get().getKakaoId());
        LoginResult loginResult = new LoginResult();
        loginResult.setAccessToken(token);
        loginResult.setMember(member.get());
        return loginResult;
    }

    //받은 액세스토큰으로 사용자 정보 받아오기
    public OAuthProfile getUserProfile(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    request,
                    String.class
            );

            ObjectMapper objectMapper = new ObjectMapper();
            OAuthProfile profile = new OAuthProfile();
            profile.setId(objectMapper.readTree(response.getBody()).get("id").asText());
            profile.setConnected_at(objectMapper.readTree(response.getBody()).get("connected_at").asText());
            profile.setProperties(objectMapper.readValue(objectMapper.readTree(response.getBody()).get("properties").toString(), OAuthProfile.Properties.class));

            return profile;
        } catch (JsonProcessingException e) {
            throw new Error("Failed to get user profile", e);
        }
    }

    //회원가입 로직
    public Member signUp(OAuthProfile profile) {
        Member newMember = new Member();
        newMember.setKakaoId(profile.getId());
        newMember.setNickname(profile.getProperties().getNickname());
        newMember.setProfileImage(profile.getProperties().getProfile_image());
        newMember.setStretching(0);
        kakaoRepository.save(newMember);

        return newMember;
    }

    //jwt 토큰 생성
    public String jwtTokenGenerator(Long id, String kakaoId) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(id, kakaoId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.createToken(authentication);

        return "Bearer " + jwt;
    }
}

