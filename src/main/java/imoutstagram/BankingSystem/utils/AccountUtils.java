package imoutstagram.BankingSystem.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_NOT_EXISTED_CODE = "000";
    public static final String ACCOUNT_NOT_EXISTED_MESSAGE = "This email doesn't exist.";

    public static final String ACCOUNT_EXISTED_CODE = "001";
    public static final String ACCOUNT_EXISTED_MESSAGE = "This email already exists.";

    public static final String ACCOUNT_CREATED = "002";
    public static final String ACCOUNT_CREATED_SUCCESSFULLY = "A new account has been created.";

    public static final String ACCOUNT_FOUND_CODE = "003";
    public static final String ACCOUNT_FOUND_MESSAGE = "Account found.";

    public static final String ACCOUNT_DELETED_CODE = "004";
    public static final String ACCOUNT_DELETED_MESSAGE = "This account has been deleted.";

    public static final String NOT_ENOUGH_BALANCES_CODE = "005";
    public static final String NOT_ENOUGH_BALANCES_MESSAGE = "You don't have enough balances to perform this transaction.";

    public static final String OPERATION_SUCCESSFUL_CODE = "040";
    public static final String OPERATION_SUCCESSFUL_MESSAGE = "Operation successful.";

    public static final String OPERATION_FAILED_CODE = "404";
    public static final String OPERATION_FAILED_MESSAGE = "Operation failed. Please try again.";


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
