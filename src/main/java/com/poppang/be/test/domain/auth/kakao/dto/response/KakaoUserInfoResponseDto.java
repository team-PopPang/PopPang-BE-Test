package com.poppang.be.test.domain.auth.kakao.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poppang.be.test.domain.users.entity.Users;
import lombok.Getter;

import java.util.Map;

@Getter
public class KakaoUserInfoResponseDto {

    private Long id;
    @JsonProperty("kakao_account")
    private Map<String, Object> kakaoAccount;

    public Users toEntity() {
        return Users.builder()
                .uid(String.valueOf(id))
                .build();
    }

}
