package imoutstagram.BankingSystem.service;

import imoutstagram.BankingSystem.dto.AccountInfo;
import imoutstagram.BankingSystem.dto.BankResponse;
import imoutstagram.BankingSystem.dto.EmailDetails;
import imoutstagram.BankingSystem.dto.UserRequest;
import imoutstagram.BankingSystem.model.User;
import imoutstagram.BankingSystem.repository.UserRepository;
import imoutstagram.BankingSystem.utils.AccountUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public BankResponse createAccount(UserRequest userRequest) {
        if (this.userRepository.existsByEmail(userRequest.getEmail())) {
            BankResponse bankResponse = BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTED_CODE)
                    .message(AccountUtils.ACCOUNT_EXISTED_MESSAGE)
                    .build();
            return bankResponse;
        }
        User user = User
                .builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .address(userRequest.getAddress())
                .accountNumber(AccountUtils.generateAccountNumber())
                .balance(BigDecimal.ZERO)
                .status("Active")
                .build();
        User savedUser = userRepository.save(user);

        // send mail to new user
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Welcome, " + savedUser.getFirstName() + " " + savedUser.getLastName())
                .body("We are so glad to have your belief!\n" +
                        "Your account details are: \n" +
                        "Account name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + ",\n" +
                        "Account number: " + savedUser.getAccountNumber() + ".\n" +
                        "Hope that you are satisfied with our services!")
                .build();
        emailService.sendEmail(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATED)
                .message(AccountUtils.ACCOUNT_CREATED_SUCCESSFULLY)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName())
                        .build())
                .build();
    }
}
