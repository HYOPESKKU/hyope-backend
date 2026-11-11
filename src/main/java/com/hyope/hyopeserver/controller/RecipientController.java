package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.recipient.RecipientRequest;
import com.hyope.hyopeserver.dto.recipient.RecipientResponse;
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
    @GetMapping("/recipients/{id}")
    public ResponseEntity<RecipientResponse> read(@PathVariable Long id){
        return ResponseEntity.ok(recipientService.read(id));
    }
    @GetMapping("/guardians/{guardianId}/recipients")
    public ResponseEntity<List<RecipientResponse>> readAll(@PathVariable Long guardianId){
        return ResponseEntity.ok(recipientService.readAll(guardianId));
    }
    //POST
    @PostMapping("/guardians/{guardianId}/recipients")
    public ResponseEntity<RecipientResponse> create(@PathVariable Long guardianId, @RequestBody RecipientRequest reqDto){
        RecipientResponse created = recipientService.create(guardianId, reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    //PATCH
    @PatchMapping("/recipients/{id}")
    public ResponseEntity<RecipientResponse> update(@PathVariable Long id, @RequestBody RecipientRequest reqDto){
        RecipientResponse updated = recipientService.update(id, reqDto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }
    // "지금 바로 통화걸기" — 예약 시간과 무관하게 즉시 통화를 트리거한다.
    @PostMapping("/recipients/{id}/call-now")
    public ResponseEntity<Void> callNow(@PathVariable Long id){
        recipientService.callNow(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
    //DELETE
    @DeleteMapping("/recipients/{id}")
    public ResponseEntity<RecipientResponse> delete(@PathVariable Long id){
        RecipientResponse deleted = recipientService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleted);
    }
}
