package com.poppang.be.test.domain.auth.kakao.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poppang.be.test.common.enums.Role;
import com.poppang.be.test.domain.users.entity.Provider;
import lombok.Getter;

import java.util.List;

@Getter
public class SignupRequestDto {

    private String uid;
    private Provider provider;
    private String email;
    private String nickname;
    private Role role;

    @JsonProperty("isAlerted")
    private boolean alerted;
    private String fcmToken;
    private List<String> keywordList;
    private List<Long> recommendList;

}
