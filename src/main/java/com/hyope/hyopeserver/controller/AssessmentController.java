package com.hyope.hyopeserver.controller;

import com.hyope.hyopeserver.dto.request.AssessmentRequestDto;
import com.hyope.hyopeserver.dto.response.AssessmentResponseDto;
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
    @GetMapping("/assessment/{id}")
    public ResponseEntity<AssessmentResponseDto> read(@PathVariable Long id){
        return ResponseEntity.ok(assessmentService.read(id));
    }

    @GetMapping("/recipient/{recipientId}/assessment")
    public ResponseEntity<List<AssessmentResponseDto>> readAll(@PathVariable Long recipientId){
        return ResponseEntity.ok(assessmentService.readAll(recipientId));
    }

    //POST
    @PostMapping("/recipient/{recipientId}/call-log/{callLogId}/assessment")
    public ResponseEntity<AssessmentResponseDto> create(@PathVariable Long recipientId,
                                                        @PathVariable Long callLogId,
                                                        @RequestBody AssessmentRequestDto reqDto){
        AssessmentResponseDto created = assessmentService.create(recipientId, callLogId, reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //DELETE
    @DeleteMapping("/assessment/{id}")
    public ResponseEntity<AssessmentResponseDto> delete(@PathVariable Long id){
        AssessmentResponseDto deleted = assessmentService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(deleted);
    }
}