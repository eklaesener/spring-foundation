package de.eklaesener.inventorizer.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private static final String FORWARD = "forward:/";

    public MvcConfig() {

    }

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/{spring:\\w+}")
            .setViewName(FORWARD);
        registry.addViewController("/**/{spring:\\w+}")
            .setViewName(FORWARD);
        registry.addViewController("/{spring:\\w+}/**{spring:?!(\\.js|\\.css)$}")
            .setViewName(FORWARD);
    }
}
