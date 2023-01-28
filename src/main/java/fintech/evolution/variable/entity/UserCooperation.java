package fintech.evolution.variable.entity;

import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
@Table(name = "user_cooperation")
@Getter
@Setter
public class UserCooperation extends BaseEntity {

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "attach_id", columnDefinition = "TEXT")
    private String attachId;

}
