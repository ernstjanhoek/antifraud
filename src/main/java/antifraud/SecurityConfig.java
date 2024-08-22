package antifraud;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf(CsrfConfigurer::disable)
                .cors(CorsConfigurer::disable)
                .exceptionHandling(handing -> handing
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                )
                .headers(headers -> headers.frameOptions().disable())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/auth/user").permitAll()
                        .requestMatchers("/actuator/shutdown").permitAll()
                        .requestMatchers("/api/auth/user").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasAnyAuthority("MERCHANT")
                        .requestMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasAnyAuthority("SUPPORT")
                        .requestMatchers("/api/antifraud/history/**").hasAnyAuthority("SUPPORT")
                        .requestMatchers("/api/antifraud/history").hasAnyAuthority("SUPPORT")
                        .requestMatchers("/api/antifraud/suspicious-ip/**").hasAnyAuthority("SUPPORT")
                        .requestMatchers("/api/antifraud/suspicious-ip").hasAnyAuthority("SUPPORT")
                        .requestMatchers("/api/antifraud/stolencard/**").hasAnyAuthority("SUPPORT")
                        .requestMatchers("/api/antifraud/stolencard").hasAnyAuthority("SUPPORT")
                        .requestMatchers("/api/auth/list").hasAnyAuthority("SUPPORT", "ADMINISTRATOR")
                        .requestMatchers("/api/auth/**").hasAuthority("ADMINISTRATOR")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Component
    public static class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request,
                             HttpServletResponse response,
                             AuthenticationException authException) throws IOException, ServletException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        }
    }
}
