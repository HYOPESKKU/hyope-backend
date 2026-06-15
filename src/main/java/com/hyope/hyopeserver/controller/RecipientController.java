package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.request.RecipientRequestDto;
import com.hyope.hyopeserver.dto.response.RecipientResponseDto;
import com.hyope.hyopeserver.service.RecipientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class RecipientController {
    private final RecipientService recipientService;
    //GET
    @GetMapping("/recipient/{id}")
    public ResponseEntity<RecipientResponseDto> read(@PathVariable Long id){
        return ResponseEntity.ok(recipientService.read(id));
    }
    @GetMapping("/guardian/{guardianId}/recipient")
    public ResponseEntity<List<RecipientResponseDto>> readAll(@PathVariable Long guardianId){
        return ResponseEntity.ok(recipientService.readAll(guardianId));
    }
    //POST
    @PostMapping("/guardian/{guardianId}/recipient")
    public ResponseEntity<RecipientResponseDto> create(@PathVariable Long guardianId, @RequestBody RecipientRequestDto reqDto){
        RecipientResponseDto created = recipientService.create(guardianId, reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    //PATCH
    @PatchMapping("/recipient/{id}")
    public ResponseEntity<RecipientResponseDto> update(@PathVariable Long id, @RequestBody RecipientRequestDto reqDto){
        RecipientResponseDto updated = recipientService.update(id, reqDto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }
    //DELETE
    @DeleteMapping("/recipient/{id}")
    public ResponseEntity<RecipientResponseDto> delete(@PathVariable Long id){
        RecipientResponseDto deleted = recipientService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleted);
    }
}
