package com.safecall.safecall.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guardian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_member_id")
    private Member guardianMember;

    private String relationship;

    private LocalDateTime createdAt;

    @Builder
    public Guardian(Member member, Member guardianMember, String relationship) {
        this.member = member;
        this.guardianMember = guardianMember;
        this.relationship = relationship;
        this.createdAt = LocalDateTime.now();
    }
}