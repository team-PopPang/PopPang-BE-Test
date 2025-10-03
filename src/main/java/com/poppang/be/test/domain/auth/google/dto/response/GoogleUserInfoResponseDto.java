package com.poppang.be.test.domain.auth.google.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class GoogleUserInfoResponseDto {

    private String sub; // uid
    private String email;
    private String name;

    @JsonProperty("picture")
    private String pictureUrl;

    public GoogleUserInfoResponseDto(String sub,
                                     String email,
                                     String name,
                                     String pictureUrl) {
        this.sub = sub;
        this.email = email;
        this.name = name;
        this.pictureUrl = pictureUrl;
    }

}
