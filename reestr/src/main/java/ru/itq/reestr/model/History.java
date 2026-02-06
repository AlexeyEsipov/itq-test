package ru.itq.reestr.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "histories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "histories_seq"
    )
    @SequenceGenerator(
            name = "histories_seq",
            sequenceName = "histories_id_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(name = "doc_id")
    private Long docId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private Action action;

    @Column(name = "action_by")
    private String actionBy;

    @Column(name = "action_at")
    private OffsetDateTime actionAt;

    @Column(name = "description")
    private String description;

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", action=" + action +
                ", actionBy='" + actionBy + '\'' +
                ", actionAt=" + actionAt +
                ", description='" + description + '\'' +
                '}';
    }
}