package com.smartexpense.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component                                    // ← registers as Spring bean
@ConfigurationProperties(prefix = "jwt")      // ← binds jwt.secret + jwt.expiration
@Getter
@Setter
public class JwtConfig {
    private String secret;
    private long expiration;
}

