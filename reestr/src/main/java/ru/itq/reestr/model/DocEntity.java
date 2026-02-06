package ru.itq.reestr.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "docs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "docs_seq"
    )
    @SequenceGenerator(
            name = "docs_seq",
            sequenceName = "docs_id_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(name = "inner_id")
    private String innerId;

    @Column(name = "title")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

   /* @Version
    private Long version;*/
}