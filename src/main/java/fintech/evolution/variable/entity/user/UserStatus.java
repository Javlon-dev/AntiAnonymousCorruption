package fintech.evolution.variable.entity.user;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;

@Getter
@Setter
@Entity
public class UserStatus {
    @Id
    private Long chatId;
    private Boolean status;
    private Date closeDate;
}
