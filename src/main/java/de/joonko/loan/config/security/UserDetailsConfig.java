package de.joonko.loan.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
public class UserDetailsConfig {

    @Value("${security.basic.user.password}")
    private String userPassword;

    @Value("${security.basic.user.username}")
    private String userUsername;

    @Value("${security.basic.postbank.password}")
    private String pbPassword;

    @Value("${security.basic.postbank.username}")
    private String pbUsername;

    @Value("${security.basic.reporting.password}")
    private String reportingPassword;

    @Value("${security.basic.reporting.username}")
    private String reportingUsername;

    @Value("${security.basic.admin.password}")
    private String adminPassword;

    @Value("${security.basic.admin.username}")
    private String adminUsername;

    @Value("${security.basic.internal.password}")
    private String internalUserPassword;

    @Value("${security.basic.internal.username}")
    private String internalUserUsername;

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        var userDetails = List.of(
                createUser(pbUsername, pbPassword, SecurityRole.PB_USER),
                createUser(reportingUsername, reportingPassword, SecurityRole.REPORTING),
                createUser(adminUsername, adminPassword, SecurityRole.ADMIN),
                createUser(userUsername, userPassword, SecurityRole.USER),
                createUser(internalUserUsername, internalUserPassword, SecurityRole.INTERNAL));
        return new MapReactiveUserDetailsService(userDetails);
    }

    private UserDetails createUser(String userName, String password, SecurityRole... roles) {
        return User.withUsername(userName)
                .password(getEncoder().encode(password))
                .roles(Arrays.stream(roles).map(Enum::name).toArray(String[]::new)).build();
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
