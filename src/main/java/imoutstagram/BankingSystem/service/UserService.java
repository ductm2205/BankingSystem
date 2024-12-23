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
        if (!isAccountExists(userRequest.getAccountNumber())) {
            return createAccountNotExistResponse();
        }
        // find in db
        User user = userRepository.findByAccountNumber(userRequest.getAccountNumber());
        // return
        return createAccountExistResponse(user);
    }

    public String nameEnquiry(EnquiryRequest userRequest) {
        // check if exist
        if (!isAccountExists(userRequest.getAccountNumber())) {
            return AccountUtils.ACCOUNT_NOT_EXISTED_MESSAGE;
        }

        // find in db
        User user = userRepository.findByAccountNumber(userRequest.getAccountNumber());

        return user.getFirstName() + " " + user.getLastName();
    }

    public BankResponse creditAccount(CreditDebitRequest request) {
        // check if exist
        if (!isAccountExists(request.getAccountNumber())) {
            return createAccountNotExistResponse();
        }

        // find in db
        User user = userRepository.findByAccountNumber(request.getAccountNumber());

        // unable to perform transaction
        if (hasInsufficientBalance(user, request.getAmount())) {
            return createNotEnoughBalanceResponse(user);
        }

        // update balance
        boolean isCredited = credit(user, request.getAmount());

        if (!isCredited) {
            return createOperationFailedResponse();
        }

        // send notification
        EmailDetails emailDetails = creditEmail(user, request.getAmount());
        emailService.sendEmail(emailDetails);

        // resp
        return createOperationSuccessfulResponse(user);
    }

    public BankResponse debitAccount(CreditDebitRequest request) {
        if (!isAccountExists(request.getAccountNumber())) {
            return createAccountNotExistResponse();
        }

        User user = userRepository.findByAccountNumber(request.getAccountNumber());

        if (hasInsufficientBalance(user, request.getAmount())) {
            return createNotEnoughBalanceResponse(user);
        }

        boolean isDebited = debit(user, request.getAmount());

        if (!isDebited) {
            return createOperationFailedResponse();
        }

        // send notification
        EmailDetails emailDetails = debitEmail(user, request.getAmount());
        emailService.sendEmail(emailDetails);

        return createOperationSuccessfulResponse(user);
    }

    public BankResponse transfer(TransferRequest request) {

        // check if exist
        if (!isAccountExists(request.getToUser())) {
            return createAccountNotExistResponse();
        }
        if (!isAccountExists(request.getFromUser())) {
            return createAccountNotExistResponse();
        }

        // perform transaction
        User from = userRepository.findByAccountNumber(request.getFromUser());

        // unable to perform transaction
        if (hasInsufficientBalance(from, request.getAmount())) {
            return createNotEnoughBalanceResponse(from);
        }

        // update balance
        boolean isDebited = debit(from, request.getAmount());

        if (!isDebited) {
            return createOperationFailedResponse();
        }

        User to = userRepository.findByAccountNumber(request.getToUser());

        //
        boolean isCredited = credit(to, request.getAmount());
        if (!isCredited) {
            return createOperationFailedResponse();
        }

        EmailDetails debit = debitEmail(from, request.getAmount());
        EmailDetails credit = creditEmail(to, request.getAmount());
        emailService.sendEmail(credit);
        emailService.sendEmail(debit);

        return createOperationSuccessfulResponse(from);
    }

    private boolean isAccountExists(String accountNumber) {
        return userRepository.existsByAccountNumber(accountNumber);
    }

    private boolean hasInsufficientBalance(User user, BigDecimal amount) {
        return user.getBalance().compareTo(amount) < 0;
    }

    private boolean credit(User user, BigDecimal amount) {
        //
        try {
            user.setBalance(user.getBalance().add(amount));
            userRepository.save(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    private boolean debit(User user, BigDecimal amount) {
        //
        try {
            user.setBalance(user.getBalance().subtract(amount));
            userRepository.save(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    private EmailDetails creditEmail(User to, BigDecimal amount) {
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(to.getEmail())
                .subject("Your account has been credited, " + to.getFirstName() + " " + to.getLastName())
                .body("A credit has just been made to your account!\n Here's the detail:\n" +
                        "Credit amount: " + amount + "\n" +
                        "Your account balance has been updated: " + to.getBalance() + "")
                .build();
        emailService.sendEmail(emailDetails);
        return emailDetails;
    }

    private EmailDetails debitEmail(User to, BigDecimal amount) {
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(to.getEmail())
                .subject("Your account has been debited, " + to.getFirstName() + " " + to.getLastName())
                .body("A debit has just been made to your account!\n Here's the detail:\n" +
                        "Debit amount: " + amount + "\n" +
                        "Your account balance has been updated: " + to.getBalance() + "")
                .build();
        return emailDetails;
    }

    private BankResponse createAccountNotExistResponse() {
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_NOT_EXISTED_CODE)
                .message(AccountUtils.ACCOUNT_NOT_EXISTED_MESSAGE)
                .build();
    }

    private BankResponse createAccountExistResponse(User user) {
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

    private BankResponse createNotEnoughBalanceResponse(User user) {
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

    private BankResponse createOperationFailedResponse() {
        return BankResponse.builder()
                .responseCode(AccountUtils.OPERATION_FAILED_CODE)
                .message(AccountUtils.OPERATION_FAILED_MESSAGE)
                .build();
    }

    private BankResponse createOperationSuccessfulResponse(User user) {
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
