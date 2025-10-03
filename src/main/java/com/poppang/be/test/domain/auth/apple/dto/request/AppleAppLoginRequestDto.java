package com.poppang.be.test.domain.auth.apple.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppleAppLoginRequestDto {

    @JsonProperty("auth_code")
    private String authCode;

}
