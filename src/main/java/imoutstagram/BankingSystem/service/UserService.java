package imoutstagram.BankingSystem.service;

import imoutstagram.BankingSystem.dto.*;
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
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTED_CODE)
                    .message(AccountUtils.ACCOUNT_EXISTED_MESSAGE)
                    .build();
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

    public BankResponse balanceEnquiry(EnquiryRequest userRequest) {
        // check if exist
        if (!userRepository.existsByAccountNumber(userRequest.getAccountNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTED_CODE)
                    .message(AccountUtils.ACCOUNT_NOT_EXISTED_MESSAGE)
                    .build();
        }
        // find in db
        User user = userRepository.findByAccountNumber(userRequest.getAccountNumber());
        // return
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .message(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(user.getFirstName() + " " + user.getLastName())
                        .accountNumber(user.getAccountNumber())
                        .accountBalance(user.getBalance())
                        .build())
                .build();
    }

    public String nameEnquiry(EnquiryRequest userRequest) {
        // check if exist
        if (!userRepository.existsByAccountNumber(userRequest.getAccountNumber())) {
            return AccountUtils.ACCOUNT_NOT_EXISTED_MESSAGE;
        }

        // find in db
        User user = userRepository.findByAccountNumber(userRequest.getAccountNumber());

        return user.getFirstName() + " " + user.getLastName();
    }

    public BankResponse creditAccount(CreditDebitRequest request) {
        // check if exist
        if (!userRepository.existsByAccountNumber(request.getAccountNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTED_CODE)
                    .message(AccountUtils.ACCOUNT_NOT_EXISTED_MESSAGE)
                    .build();
        }

        // find in db
        User user = userRepository.findByAccountNumber(request.getAccountNumber());

        // unable to perform transaction
        if (user.getBalance().compareTo(request.getAmount().abs()) < 0 && request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.NOT_ENOUGH_BALANCES_CODE)
                    .message(AccountUtils.NOT_ENOUGH_BALANCES_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(user.getAccountNumber())
                            .accountName(user.getFirstName() + " " + user.getLastName())
                            .accountBalance(user.getBalance())
                            .build())
                    .build();
        }

        // update balance
        try {
            user.setBalance(user.getBalance().add(request.getAmount()));
            userRepository.save(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return BankResponse.builder()
                    .responseCode(AccountUtils.OPERATION_FAILED_CODE)
                    .message(AccountUtils.OPERATION_FAILED_MESSAGE)
                    .build();
        }
        // resp
        return BankResponse.builder()
                .responseCode(AccountUtils.OPERATION_SUCCESSFUL_CODE)
                .message(AccountUtils.OPERATION_SUCCESSFUL_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(user.getAccountNumber())
                        .accountName(user.getFirstName() + " " + user.getLastName())
                        .accountBalance(user.getBalance())
                        .build())
                .build();
    }
}
