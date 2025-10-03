package com.poppang.be.test.domain.auth.apple.dto.response;

import lombok.Getter;

@Getter
public class AppleUserInfoResponseDto {

    private String sub;
    private String uid;
    private String email;

    public AppleUserInfoResponseDto(String sub,
                                    String uid,
                                    String email) {
        this.sub = sub;
        this.uid = uid;
        this.email = email;
    }

}
