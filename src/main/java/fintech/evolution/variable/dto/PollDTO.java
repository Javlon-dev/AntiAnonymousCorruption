package fintech.evolution.variable.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PollDTO {
    private Long chatId;
    private String question;
    private List<String> options;
    private Integer correctOptionId;
    private String explain;
    private String type;
    private Boolean isAnonymous;

    public PollDTO(String type, Boolean isAnonymous) {
        this.type = type;
        this.isAnonymous = isAnonymous;
    }
}
