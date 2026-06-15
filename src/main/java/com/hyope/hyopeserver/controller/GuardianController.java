package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.request.GuardianRequestDto;
import com.hyope.hyopeserver.dto.response.GuardianResponseDto;
import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.service.GuardianService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class GuardianController {
    private final GuardianService guardianService;
    //GET
    @GetMapping("/guardian")
    public List<GuardianResponseDto> readAll(){
        return guardianService.readAll().stream()
                .map(GuardianResponseDto::createGuardianResponseDto)
                .toList();
    }
    @GetMapping("/guardian/{id}")
    public ResponseEntity<GuardianResponseDto> read(@PathVariable Long id){
        Guardian guardian = guardianService.read(id);
        return ResponseEntity.ok(GuardianResponseDto.createGuardianResponseDto(guardian));
    }
    //POST
    @PostMapping("/guardian")
    public ResponseEntity<GuardianResponseDto> create(@RequestBody GuardianRequestDto dto){
        Guardian created = guardianService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GuardianResponseDto.createGuardianResponseDto(created));
    }
    //PATCH
    @PatchMapping("/guardian/{id}")
    public ResponseEntity<GuardianResponseDto> update(@PathVariable Long id, @RequestBody GuardianRequestDto dto){
        Guardian updated = guardianService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GuardianResponseDto.createGuardianResponseDto(updated));

    }
    //DELETE
    @DeleteMapping("/guardian/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        guardianService.delete(id);
        return ResponseEntity.noContent().build();
    }
    //login
    @PostMapping("/guardian/login")
    public ResponseEntity<GuardianResponseDto> login(@RequestBody GuardianRequestDto dto){
        Guardian guardian = guardianService.login(dto.getLoginId(), dto.getPassword());
        return ResponseEntity.ok(GuardianResponseDto.createGuardianResponseDto(guardian));
    }
}
