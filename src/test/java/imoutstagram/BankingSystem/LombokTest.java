package imoutstagram.BankingSystem;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LombokTest {
    private String testField;

    public static void main(String[] args) {
        LombokTest test = LombokTest.builder()
                .testField("Hello, Lombok!")
                .build();

        System.out.println(test);
    }
}
