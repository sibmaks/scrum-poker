package com.github.sibmaks.sp.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring security config
 *
 * @author sibmaks
 * Created at 28-09-2021
 */
@Configuration
@EnableWebSecurity
public class SecurityConf {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .authorizeRequests()
                .antMatchers("/api/**").permitAll();
        return http.build();
    }
}
