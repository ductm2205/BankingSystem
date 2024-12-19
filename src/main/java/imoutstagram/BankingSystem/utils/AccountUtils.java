package imoutstagram.BankingSystem.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXISTED_CODE = "001";
    public static final String ACCOUNT_EXISTED_MESSAGE = "This email already exists.";

    public static final String ACCOUNT_CREATED = "002";
    public static final String ACCOUNT_CREATED_SUCCESSFULLY = "A new account has been created.";

    // random account number
    public static String generateAccountNumber() {
        Year year = Year.now();
        int min = 100000;
        int max = 999999;

        int random = (int) Math.floor(Math.random() * (max - min + 1));

        String randomNumber = String.valueOf(random);
        String yearString = String.valueOf(year);

        StringBuilder accountNumber = new StringBuilder();
        accountNumber.append(yearString);
        accountNumber.append(randomNumber);
        return accountNumber.toString();
    }

}
