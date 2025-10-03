package com.poppang.be.test.domain.auth.kakao.application;

import com.poppang.be.test.common.enums.Role;
import com.poppang.be.test.domain.auth.kakao.config.KakaoProperties;
import com.poppang.be.test.domain.auth.kakao.dto.request.KakaoAppLoginRequestDto;
import com.poppang.be.test.domain.auth.kakao.dto.request.SignupRequestDto;
import com.poppang.be.test.domain.auth.kakao.dto.response.KakaoTokenResponseDto;
import com.poppang.be.test.domain.auth.kakao.dto.response.KakaoUserInfoResponseDto;
import com.poppang.be.test.domain.auth.dto.response.LoginResponseDto;
import com.poppang.be.test.domain.auth.dto.response.SignupResponseDto;
import com.poppang.be.test.domain.keyword.entity.UserKeyword;
import com.poppang.be.test.domain.keyword.infrastructure.UserKeywordRepository;
import com.poppang.be.test.domain.recommend.entity.Recommend;
import com.poppang.be.test.domain.recommend.entity.UserRecommend;
import com.poppang.be.test.domain.recommend.infrastructure.RecommendRepository;
import com.poppang.be.test.domain.recommend.infrastructure.UserRecommendRepository;
import com.poppang.be.test.domain.users.entity.Provider;
import com.poppang.be.test.domain.users.entity.Users;
import com.poppang.be.test.domain.users.infrastructure.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final KakaoProperties kakaoProperties;
    private final RestTemplate restTemplate = new RestTemplate();
    private final UsersRepository usersRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final UserRecommendRepository userRecommendRepository;
    private final RecommendRepository recommendRepository;

    // Web 로그인
    @Transactional
    public LoginResponseDto webLogin(String authCode) {
        KakaoTokenResponseDto kakaoToken = getAccessToken(authCode);
        if (kakaoToken == null || kakaoToken.getAccessToken() == null || kakaoToken.getAccessToken().isBlank()) {
            throw new IllegalStateException("Failed to retrieve Kakao access token");
        }

        KakaoUserInfoResponseDto kakaoUserInfoResponseDto = getUserInfo(kakaoToken.getAccessToken());
        String uid = String.valueOf(kakaoUserInfoResponseDto.getId());

        Users user = upsertByUid(uid);

        return LoginResponseDto.from(user);
    }

    // App 로그인
    public LoginResponseDto mobileLogin(KakaoAppLoginRequestDto kakaoAppLoginRequestDto) {

        KakaoUserInfoResponseDto kakaoUserInfoResponseDto = getUserInfo(kakaoAppLoginRequestDto.getAccessToken());
        String uid = String.valueOf(kakaoUserInfoResponseDto.getId());

        Users user = upsertByUid(uid);

        return LoginResponseDto.from(user);
    }

    // 회원가입
    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {

        // 닉네임 중복 확인
        if (usersRepository.existsByNickname(signupRequestDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다. ");
        }

        Users user = usersRepository.findByUid(signupRequestDto.getUid())
                .orElseThrow(() -> new IllegalStateException("유저를 찾을 수 없습니다. "));

        user.completeSignup(signupRequestDto);
        usersRepository.save(user);

        // 키워드 저장
        for (String keyword : signupRequestDto.getKeywordList()) {
            userKeywordRepository.save(new UserKeyword(user, keyword));
        }

        // 추천 저장
        List<Long> recommendIds = Optional.ofNullable(signupRequestDto.getRecommendList())
                .orElseGet(List::of)
                .stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (!recommendIds.isEmpty()) {
            List<Recommend> recommendList = recommendRepository.findAllById(recommendIds);
            for (Recommend recommend : recommendList) {
                userRecommendRepository.save(new UserRecommend(user, recommend));
            }
        }
        return SignupResponseDto.from(user);
    }

    // update + insert (존재하면 값 반환, 없으면 insert 후 반환)
    private Users upsertByUid(String uid) {
        return usersRepository.findByUid(uid)
                .orElseGet(() -> usersRepository.save(
                        Users.builder()
                                .uid((uid))
                                .provider(Provider.KAKAO)
                                .role(Role.MEMBER)
                                .build()
                ));
    }

    // 1. code -> 토큰
    private KakaoTokenResponseDto getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProperties.getClientId());
        params.add("redirect_uri", kakaoProperties.getRedirectUri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponseDto> response = restTemplate.exchange(
                kakaoProperties.getTokenUri(),
                HttpMethod.POST,
                request,
                KakaoTokenResponseDto.class
        );

        return response.getBody();
    }

    // 2. 토큰 -> user info
    private KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfoResponseDto> res = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                req,
                KakaoUserInfoResponseDto.class
        );

        return res.getBody();
    }

}
