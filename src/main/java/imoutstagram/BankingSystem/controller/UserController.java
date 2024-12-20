package imoutstagram.BankingSystem.controller;

import imoutstagram.BankingSystem.dto.BankResponse;
import imoutstagram.BankingSystem.dto.CreditDebitRequest;
import imoutstagram.BankingSystem.dto.EnquiryRequest;
import imoutstagram.BankingSystem.dto.UserRequest;
import imoutstagram.BankingSystem.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }

    @GetMapping(value = "/balance")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
    }

    @GetMapping(value = "/name")
    public String nameEnquiry(@RequestBody EnquiryRequest request) {
        return userService.nameEnquiry(request);
    }

    @PutMapping(value = "/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request) {
        return userService.creditAccount(request);
    }

}
