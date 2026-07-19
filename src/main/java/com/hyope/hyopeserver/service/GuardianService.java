package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.guardian.GuardianPatchRequest;
import com.hyope.hyopeserver.dto.guardian.SignupRequest;
import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.exception.AuthenticationFailedException;
import com.hyope.hyopeserver.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GuardianService {
    private final GuardianRepository guardianRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecipientService recipientService;

    @Transactional
    public Guardian create(SignupRequest dto){
        if (guardianRepository.existsByLoginId(dto.getLoginId())) {
            throw new IllegalArgumentException("보호자 생성 실패: 이미 존재하는 로그인 ID입니다.");
        }
        return guardianRepository.save(dto.toEntity(passwordEncoder));
    }

    public Guardian read(Long id){
        return guardianRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("보호자 조회 실패: 해당 보호자가 없습니다"));
    }

    public List<Guardian> readAll(){
        return guardianRepository.findAll();
    }

    public Guardian readByLoginId(String loginId){
        return guardianRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("보호자 조회 실패: 해당 보호자가 없습니다"));
    }

    @Transactional
    public Guardian update(Long id, GuardianPatchRequest dto){
        Guardian target = guardianRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("보호자 수정 실패: 해당 보호자가 없습니다"));
        target.patch(dto.toEntity());
        return guardianRepository.save(target);
    }

    @Transactional
    public Guardian delete(Long id){
        Guardian target = guardianRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("보호자 삭제 실패: 해당 보호자가 없습니다"));
        recipientService.deleteAllByGuardianId(id);
        guardianRepository.delete(target);
        return target;
    }

    public Guardian login(String loginId, String rawPassword){
        Guardian guardian = guardianRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AuthenticationFailedException("로그인 실패: 로그인 정보가 일치하지 않습니다."));
        if(!passwordEncoder.matches(rawPassword, guardian.getPasswordHash())){
            throw new AuthenticationFailedException("로그인 실패: 로그인 정보가 일치하지 않습니다.");
        }
        return guardian;
    }
}