package com.github.sibmaks.sp.conf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.temporal.ChronoUnit;

/**
 * @author sibmaks
 * @since 0.0.2
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "app.session.ttl")
public class SessionTtlProperties {
    /**
     * User session time to a live type.
     */
    private ChronoUnit type;
    /**
     * User session time to live value.
     */
    private int value;
}
