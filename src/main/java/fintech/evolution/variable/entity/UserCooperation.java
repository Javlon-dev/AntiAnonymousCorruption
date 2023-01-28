package fintech.evolution.variable.entity;

import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.ToString;

@Entity
@Table(name = "user_cooperation")
@Getter
@Setter
@ToString
public class UserCooperation extends BaseEntity {

    @Column(name = "username")
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "attach_id")
    private Integer attachId;

}
