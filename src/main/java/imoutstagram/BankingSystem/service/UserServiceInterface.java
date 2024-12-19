package imoutstagram.BankingSystem.service;

import imoutstagram.BankingSystem.dto.BankResponse;
import imoutstagram.BankingSystem.dto.UserRequest;

public interface UserServiceInterface {
    BankResponse createAccount(UserRequest userRequest);
}
