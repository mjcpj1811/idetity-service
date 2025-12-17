package com.example.identity_service.configuration;

import com.example.identity_service.entity.User;
import com.example.identity_service.enums.Role;
import com.example.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {

                PasswordEncoder encoder = new BCryptPasswordEncoder(10);

                User user = User.builder()
                        .username("admin")
                        .password(encoder.encode("admin"))
                        .build();

                userRepository.save(user);
                log.warn("Admin user created with username: admin and password: admin");
            }
        };
    }
}

