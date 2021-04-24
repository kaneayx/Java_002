package io.homework08.kyle;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

/**
 * Spring boot properties configuration.
 */
@ConfigurationProperties(prefix = "spring.test")
@Getter
@Setter
public final class TestPropertiesConfiguration {
    
    private Properties props = new Properties();
}
