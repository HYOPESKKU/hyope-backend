package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.GuardianRequestDto;
import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.repository.GuardianRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @Transactional
    public Guardian create(GuardianRequestDto dto){
        if (guardianRepository.existsByLoginId(dto.getLoginId())) {
            throw new IllegalStateException("이미 존재하는 로그인 ID입니다.");
        }
        return guardianRepository.save(dto.toEntity(passwordEncoder));
    }

    public Guardian read(Long id){
        return guardianRepository.findById(id).orElse(null);
    }

    public List<Guardian> readAll(){
        return guardianRepository.findAll();
    }

    @Transactional
    public Guardian update(Long id, GuardianRequestDto dto){
        Guardian target = guardianRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Guardian not found: " + id));
        target.patch(dto.toEntity(passwordEncoder));
        return guardianRepository.save(target);
    }

    @Transactional
    public Guardian delete(Long id){
        Guardian target = guardianRepository.findById(id).orElse(null);
        if(target == null) return null;
        guardianRepository.delete(target);
        return target;
    }

    public Guardian login(String loginId, String rawPassword){
        Guardian guardian = guardianRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
        if(!passwordEncoder.matches(rawPassword, guardian.getPasswordHash())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return guardian;
    }

}
