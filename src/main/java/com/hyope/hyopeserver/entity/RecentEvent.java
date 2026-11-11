package com.hyope.hyopeserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recent_event")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class RecentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Recipient recipient;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDate addedAt;

    @Column(nullable = false)
    private boolean active;

    private LocalDateTime removedAt;
    private String removedReason;

    public void remove(String reason) {
        this.active = false;
        this.removedAt = LocalDateTime.now();
        this.removedReason = reason;
    }
}