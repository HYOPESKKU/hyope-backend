package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.assessment.AssessmentRequest;
import com.hyope.hyopeserver.dto.assessment.AssessmentResponse;
import com.hyope.hyopeserver.service.AssessmentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class AssessmentController {
    private final AssessmentService assessmentService;
    //GET
    @GetMapping("/assessments/{id}")
    public ResponseEntity<AssessmentResponse> read(@PathVariable Long id){
        return ResponseEntity.ok(assessmentService.read(id));
    }
    @GetMapping("/recipients/{recipientId}/assessments")
    public ResponseEntity<List<AssessmentResponse>> readAll(@PathVariable Long recipientId){
        return ResponseEntity.ok(assessmentService.readAll(recipientId));
    }
    //POST
    @PostMapping("/recipients/{recipientId}/call-logs/{callLogId}/assessments")
    public ResponseEntity<AssessmentResponse> create(@PathVariable Long recipientId,
                                                     @PathVariable Long callLogId,
                                                     @RequestBody AssessmentRequest reqDto){
        AssessmentResponse created = assessmentService.create(recipientId, callLogId, reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    //DELETE
    @DeleteMapping("/assessments/{id}")
    public ResponseEntity<AssessmentResponse> delete(@PathVariable Long id){
        AssessmentResponse deleted = assessmentService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleted);
    }
}