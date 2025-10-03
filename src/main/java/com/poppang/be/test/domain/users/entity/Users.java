package com.poppang.be.test.domain.users.entity;

import com.poppang.be.test.common.entity.BaseEntity;
import com.poppang.be.test.common.enums.Role;
import com.poppang.be.test.domain.auth.kakao.dto.request.SignupRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "uid", nullable = true, unique = true, length = 255)
    private String uid;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = true, length = 20)
    private Provider provider;

    @Column(name = "email", nullable = true, length = 255)
    private String email;

    @Column(name = "nickname", nullable = true, length = 255)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = true, length = 20)
    private Role role;

    @Column(name = "is_alerted", nullable = true)
    private boolean alerted = false;

    @Column(name = "fcm_token", nullable = true, length = 255)
    private String fcmToken;

    @Column(name = "is_deleted", nullable = true)
    private boolean deleted = false;

    @Builder
    public Users(Long id,
                 String uid,
                 Provider provider,
                 String email,
                 String nickname,
                 Role role,
                 boolean alerted,
                 String fcmToken,
                 boolean deleted) {
        this.id = id;
        this.uid = uid;
        this.provider = provider;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.alerted = alerted;
        this.fcmToken = fcmToken;
        this.deleted = deleted;
    }

    public void completeSignup(SignupRequestDto signupRequestDto) {
        this.email = signupRequestDto.getEmail();
        this.nickname = signupRequestDto.getNickname();
        this.alerted = signupRequestDto.isAlerted();
        this.fcmToken = signupRequestDto.getFcmToken();
    }

}
