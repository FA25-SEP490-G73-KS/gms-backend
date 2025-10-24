package fpt.edu.vn.gms.config;

import fpt.edu.vn.gms.entity.Account;
import fpt.edu.vn.gms.entity.Role;
import fpt.edu.vn.gms.repository.AccountRepository;
import fpt.edu.vn.gms.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner loadTestData(RoleRepository roleRepo, AccountRepository accountRepo) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Tạo role
            Role adminRole = Role.builder().roleName("ADMIN").build();
            Role userRole = Role.builder().roleName("USER").build();
            roleRepo.save(adminRole);
            roleRepo.save(userRole);

            // Tạo account khớp với controller login
            Account admin = Account.builder()
                    .phone("0909123456")
                    .password(encoder.encode("admin123")) // hash password
                    .role(adminRole)
                    .build();

            Account user = Account.builder()
                    .phone("0909988776")
                    .password(encoder.encode("user123")) // hash password
                    .role(userRole)
                    .build();

            accountRepo.save(admin);
            accountRepo.save(user);

            System.out.println("Test data created for Controller login");
        };
    }
}
