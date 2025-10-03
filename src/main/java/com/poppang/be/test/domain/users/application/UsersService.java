package com.poppang.be.test.domain.users.application;

import com.poppang.be.test.domain.users.dto.response.NicknameDuplicateResponseDto;
import com.poppang.be.test.domain.users.infrastructure.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public NicknameDuplicateResponseDto checkNicknameDuplicated(String nickname) {

        boolean duplicated = usersRepository.existsByNickname(nickname);

        return NicknameDuplicateResponseDto.from(duplicated);
    }

}
