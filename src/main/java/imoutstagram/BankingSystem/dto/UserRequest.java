package imoutstagram.BankingSystem.dto;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String firstName;
    private String lastName;

    private String email;
    private String phone;
    private String address;

    private String status;
}
