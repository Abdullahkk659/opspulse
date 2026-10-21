package com.opspulse.opspulse.config;
import com.opspulse.opspulse.entity.Tenant;
import com.opspulse.opspulse.entity.User;
import com.opspulse.opspulse.repository.TenantRepository;
import com.opspulse.opspulse.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final TenantRepository tenantRepository;
    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(TenantRepository tenantRepository,UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.tenantRepository=tenantRepository;
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public void run(String... args){
        if(tenantRepository.count()>0){
            return;
        }
        Tenant tenant=new Tenant();
        tenant.setName("Acme Logistics");
        tenantRepository.save(tenant);

        User user=new User();
        user.setEmail("admin@acme.com");
        user.setPasswordHash(passwordEncoder.encode("admin123"));
        user.setRole("ADMIN");
        user.setTenant(tenant);
        user.setName("Admin");
        userRepository.save(user);

        System.out.println(">>> SEEDED tenant id: " + tenant.getId());
        System.out.println(">>> SEEDED user id: " + user.getId());

        Tenant tenant2=new Tenant();
        tenant2.setName("Amazon Logistics");
        tenantRepository.save(tenant2);

        User user2=new User();
        user2.setEmail("admin2@amazon.com");
        user2.setPasswordHash(passwordEncoder.encode("admin123"));
        user2.setRole("ADMIN");
        user2.setTenant(tenant2);
        user2.setName("Admin2");
        userRepository.save(user2);

        System.out.println(">>> SEEDED tenant id: " + tenant2.getId());
        System.out.println(">>> SEEDED user id: " + user2.getId());

    }


}
