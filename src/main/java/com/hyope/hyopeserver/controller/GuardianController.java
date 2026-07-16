package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.guardian.GuardianPatchRequest;
import com.hyope.hyopeserver.dto.guardian.GuardianResponse;
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
@RequestMapping("/guardians")
public class GuardianController {
    private final GuardianService guardianService;

    //GET
    @GetMapping
    public List<GuardianResponse> readAll(){
        return guardianService.readAll().stream()
                .map(GuardianResponse::createGuardianResponseDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuardianResponse> read(@PathVariable Long id){
        Guardian guardian = guardianService.read(id);
        return ResponseEntity.ok(GuardianResponse.createGuardianResponseDto(guardian));
    }

    //PATCH
    @PatchMapping("/{id}")
    public ResponseEntity<GuardianResponse> update(@PathVariable Long id, @RequestBody GuardianPatchRequest dto){
        Guardian updated = guardianService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GuardianResponse.createGuardianResponseDto(updated));
    }

    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        guardianService.delete(id);
        return ResponseEntity.noContent().build();
    }
}