package ru.itq.reestr.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "approvals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocApproval {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "approval_seq"
    )
    @SequenceGenerator(
            name = "approval_seq",
            sequenceName = "approval_id_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(name = "doc_id")
    private Long innerId;

    @Column(name = "approver")
    private String approver;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision")
    private ApprovalDecision decision;

    @Column(name = "comment", length = 1000)
    private String comment;
}