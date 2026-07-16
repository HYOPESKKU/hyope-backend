package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.adherencerecord.AdherenceRecordRequest;
import com.hyope.hyopeserver.dto.adherencerecord.AdherenceRecordResponse;
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
    public ResponseEntity<AdherenceRecordResponse> read(@PathVariable Long id){
        return ResponseEntity.ok(adherenceRecordService.read(id));
    }
    @GetMapping("/assessment/{assessmentId}/adherence-record")
    public ResponseEntity<List<AdherenceRecordResponse>> readAll(@PathVariable Long assessmentId){
        return ResponseEntity.ok(adherenceRecordService.readAll(assessmentId));
    }
    //POST
    @PostMapping("/assessment/{assessmentId}/adherence-record")
    public ResponseEntity<AdherenceRecordResponse> create(@PathVariable Long assessmentId,
                                                          @RequestBody AdherenceRecordRequest reqDto){
        AdherenceRecordResponse created = adherenceRecordService.create(assessmentId, reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    //PATCH
    @PatchMapping("/adherence-record/{id}")
    public ResponseEntity<AdherenceRecordResponse> update(@PathVariable Long id,
                                                          @RequestBody AdherenceRecordRequest reqDto){
        AdherenceRecordResponse updated = adherenceRecordService.update(id, reqDto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }
    //DELETE
    @DeleteMapping("/adherence-record/{id}")
    public ResponseEntity<AdherenceRecordResponse> delete(@PathVariable Long id){
        AdherenceRecordResponse deleted = adherenceRecordService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleted);
    }
}