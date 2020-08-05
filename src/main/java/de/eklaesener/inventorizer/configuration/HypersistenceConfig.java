package de.eklaesener.inventorizer.configuration;

import io.hypersistence.optimizer.HypersistenceOptimizer;
import io.hypersistence.optimizer.core.config.JpaConfig;
import io.hypersistence.optimizer.hibernate.event.mapping.association.ElementCollectionEvent;
import io.hypersistence.optimizer.hibernate.event.mapping.cache.NaturalIdCacheEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
public class HypersistenceConfig {

    public HypersistenceConfig() {

    }

    @Bean
    public HypersistenceOptimizer hypersistenceOptimizer(final EntityManagerFactory entityManagerFactory) {
        return new HypersistenceOptimizer(new JpaConfig(entityManagerFactory)
            .setEventFilter(event -> !(
                event instanceof ElementCollectionEvent // This is just stupid, you need to ensure uniqueness anyways
                || event instanceof NaturalIdCacheEvent // This doesn't recognize valid usages
            )));
    }
}
