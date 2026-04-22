package com.project2.config;




import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig - Configures Spring Security for the application.
 *
 * Access rules:
 *   - Public pages:  /, /events/**, /register/**, /my-events, /feedback/**, /api/**
 *   - Admin pages:   /admin/** — requires login with ADMIN role
 *   - H2 Console:    /h2-console/** — allowed (for development only)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Define the admin user in memory.
     * In production, replace this with a database-backed UserDetailsService.
     *
     * Admin Credentials:
     *   Username: admin
     *   Password: admin123
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123")) // BCrypt encoded
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    /**
     * Use BCrypt for password hashing (industry standard).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure HTTP security rules.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ── Authorization Rules ────────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                // Allow H2 console access (development only)
                .requestMatchers("/h2-console/**").permitAll()
                // Allow all static resources (CSS, JS, images)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // Allow all public student-facing pages
                .requestMatchers("/", "/events/**", "/register/**",
                                 "/my-events", "/feedback/**", "/api/**").permitAll()
                // Require ADMIN role for all /admin/** pages
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Any other request requires authentication
                .anyRequest().authenticated()
            )

            // ── Form Login ────────────────────────────────────────────────────
            .formLogin(form -> form
                .loginPage("/admin/login")           // Custom login page
                .loginProcessingUrl("/admin/login")  // Spring Security processes POST here
                .defaultSuccessUrl("/admin/dashboard", true) // Redirect after login
                .failureUrl("/admin/login?error=true")       // Redirect on failure
                .permitAll()
            )

            // ── Logout ────────────────────────────────────────────────────────
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )

            // ── CSRF & Frame Options (for H2 console) ─────────────────────────
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()) // Allow H2 console frames
            );

        return http.build();
    }
}
