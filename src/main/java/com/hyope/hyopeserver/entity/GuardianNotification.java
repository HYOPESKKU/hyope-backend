package com.hyope.hyopeserver.entity;

import com.hyope.hyopeserver.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class GuardianNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "guardian_id")
    private Guardian guardian;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private Recipient recipient;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    @Builder.Default
    private boolean isRead = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void markRead() {
        this.isRead = true;
    }
}
