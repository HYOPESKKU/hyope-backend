package com.hyope.hyopeserver.entity;

import com.hyope.hyopeserver.entity.enums.NotificationReason;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @Enumerated(EnumType.STRING)
    private NotificationReason reason;

    @Builder.Default
    private boolean isRead = false;

    private LocalDateTime createdAt;

    public void markRead() {
        this.isRead = true;
    }
}