package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.request.AdherenceRecordRequestDto;
import com.hyope.hyopeserver.dto.response.AdherenceRecordResponseDto;
import com.hyope.hyopeserver.service.AdherenceRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class AdherenceRecordController {
    private final AdherenceRecordService adherenceRecordService;
    //GET
    @GetMapping("/adherence-record/{id}")
    public ResponseEntity<AdherenceRecordResponseDto> read(@PathVariable Long id){
        return ResponseEntity.ok(adherenceRecordService.read(id));
    }
    @GetMapping("/assessment/{assessmentId}/adherence-record")
    public ResponseEntity<List<AdherenceRecordResponseDto>> readAll(@PathVariable Long assessmentId){
        return ResponseEntity.ok(adherenceRecordService.readAll(assessmentId));
    }
    //POST
    @PostMapping("/assessment/{assessmentId}/adherence-record")
    public ResponseEntity<AdherenceRecordResponseDto> create(@PathVariable Long assessmentId,
                                                             @RequestBody AdherenceRecordRequestDto reqDto){
        AdherenceRecordResponseDto created = adherenceRecordService.create(assessmentId, reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    //PATCH
    @PatchMapping("/adherence-record/{id}")
    public ResponseEntity<AdherenceRecordResponseDto> update(@PathVariable Long id,
                                                             @RequestBody AdherenceRecordRequestDto reqDto){
        AdherenceRecordResponseDto updated = adherenceRecordService.update(id, reqDto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }
    //DELETE
    @DeleteMapping("/adherence-record/{id}")
    public ResponseEntity<AdherenceRecordResponseDto> delete(@PathVariable Long id){
        AdherenceRecordResponseDto deleted = adherenceRecordService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleted);
    }
}