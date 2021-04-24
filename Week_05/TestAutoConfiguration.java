package io.homework08.kyle;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring boot starter configuration.
 */
@Configuration
@ComponentScan("io.homework08.kyle")
@EnableConfigurationProperties(TestPropertiesConfiguration.class)
@RequiredArgsConstructor
public class TestAutoConfiguration  {

    private final TestPropertiesConfiguration props;

    @Bean
    public School getSchool() {
        return new School();
    }
}
