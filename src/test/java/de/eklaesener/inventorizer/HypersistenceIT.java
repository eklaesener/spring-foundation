package de.eklaesener.inventorizer;

import io.hypersistence.optimizer.HypersistenceOptimizer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class HypersistenceIT {

    @Autowired
    private HypersistenceOptimizer hypersistenceOptimizer;

    @Test
    public void test() {
        assertTrue(hypersistenceOptimizer.getEvents().isEmpty());
    }
}
