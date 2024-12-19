package imoutstagram.BankingSystem.service;

import imoutstagram.BankingSystem.dto.AccountInfo;
import imoutstagram.BankingSystem.dto.BankResponse;
import imoutstagram.BankingSystem.dto.UserRequest;
import imoutstagram.BankingSystem.model.User;
import imoutstagram.BankingSystem.repository.UserRepository;
import imoutstagram.BankingSystem.utils.AccountUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
