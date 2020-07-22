package de.eklaesener.inventorizer.configuration;

import io.hypersistence.optimizer.HypersistenceOptimizer;
import io.hypersistence.optimizer.core.config.JpaConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
public class HypersistenceConfig {

    public HypersistenceConfig() {

    }

    @Bean
    public HypersistenceOptimizer hypersistenceOptimizer(final EntityManagerFactory entityManagerFactory) {
        return new HypersistenceOptimizer(new JpaConfig(entityManagerFactory));
    }
}
