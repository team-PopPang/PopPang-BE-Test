package com.poppang.be.test.domain.auth.application;

import com.poppang.be.test.domain.auth.dto.request.AutoLoginRequestDto;
import com.poppang.be.test.domain.auth.dto.response.LoginResponseDto;
import com.poppang.be.test.domain.users.entity.Users;
import com.poppang.be.test.domain.users.infrastructure.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;

    public LoginResponseDto autoLogin(AutoLoginRequestDto autoLoginRequestDto) {

        Users user = usersRepository.findByUidAndDeletedFalse(autoLoginRequestDto.getUid())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. "));

        return LoginResponseDto.from(user);
    }

}
