package imoutstagram.BankingSystem;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Simple Banking APIs System",
                version = "v1.0",
                contact = @Contact(
                        name = "ductm2205",
                        url = "https://github.com/ductm2205"
                )

        )
)
public class BankingSystemApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure().load();
        // Load variables into system properties
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        SpringApplication.run(BankingSystemApplication.class, args);
    }

}
