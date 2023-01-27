package fintech.evolution.variable.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@ToString
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private String fatherName;
    private String phoneNumber;
    private String email;
    private String region;
    private String district;
    private Long stir;
    private String subjectName;
    private String serviceType;
    private String productName;
    private String operatorName;
    private Integer registeredYear;
    private String area;
    private Integer activityYear;

    @CreatedDate
    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "TIMESTAMP default NOW()")
    private LocalDateTime createdAt;
}