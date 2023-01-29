package fintech.evolution.variable.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

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

    @Transient
    private List<Integer> messagesId = new ArrayList<>();

    public void setMessagesId(Integer messageId) {
        this.messagesId.add(messageId);
    }

    public void clearMessagesId() {
        this.messagesId = new ArrayList<>();
    }
}
