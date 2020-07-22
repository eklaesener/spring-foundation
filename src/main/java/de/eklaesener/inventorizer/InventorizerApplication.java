package de.eklaesener.inventorizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@EnableJpaRepositories
@EnableTransactionManagement
@EnableScheduling
@SpringBootApplication
public class InventorizerApplication { //NOPMD

    public static void main(final String[] args) {
        SpringApplication.run(InventorizerApplication.class, args);
    }

}
