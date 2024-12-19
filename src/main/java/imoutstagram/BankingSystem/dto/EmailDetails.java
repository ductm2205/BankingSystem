package imoutstagram.BankingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDetails {
    private String recipient;

    private String subject;
    private String body;
    private String attachment;

}
