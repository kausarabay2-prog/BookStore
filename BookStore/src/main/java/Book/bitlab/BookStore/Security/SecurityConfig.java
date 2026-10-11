package Book.bitlab.BookStore.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // API үшін таза JSON жауап (401 / 403)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(401);
                            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            res.setCharacterEncoding("UTF-8");
                            res.getWriter().write("{\"error\":\"Unauthorized\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(403);
                            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            res.setCharacterEncoding("UTF-8");
                            res.getWriter().write("{\"error\":\"Forbidden\"}");
                        })
                )

                .authorizeHttpRequests(auth -> auth

                        // HTML беттер және статикалық файлдар.
                        // Беттің өзі ашық, ал деректер API арқылы қорғалған.
                        // Браузер бетті ашқанда Authorization header жібермейді,
                        // сондықтан /admin, /cart, /details ашық болуы керек.
                        .requestMatchers(
                                "/", "/books",
                                "/index.html",
                                "/login", "/register", "/cart", "/admin",
                                "/details", "/details/**",
                                "/*.html",
                                "/favicon.ico", "/error",
                                "/css/**", "/js/**", "/images/**"
                        ).permitAll()

                        // Login / Register
                        .requestMatchers("/api/auth/**").permitAll()

                        // Кітаптар: оқу — барлығына, өзгерту — ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/books", "/api/books/**").permitAll()
                        .requestMatchers("/api/books/**").hasRole("ADMIN")

                        // Категориялар: оқу — барлығына, өзгерту — ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/categories", "/api/categories/**").permitAll()
                        .requestMatchers("/api/categories/**").hasRole("ADMIN")

                        // Себет — тек кірген пайдаланушы
                        .requestMatchers("/api/cart", "/api/cart/**").authenticated()

                        // Тапсырыс жасау — кірген пайдаланушы
                        .requestMatchers(HttpMethod.POST, "/api/orders").authenticated()

                        // Қалған тапсырыс операциялары — ADMIN
                        .requestMatchers("/api/orders", "/api/orders/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}