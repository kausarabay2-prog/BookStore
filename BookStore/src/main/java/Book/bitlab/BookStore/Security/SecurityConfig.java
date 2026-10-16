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
@EnableWebSecurity//включает настройку Spring Security для веб-приложения.
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {//интерфейс для хеширования и проверки паролей.
        return new BCryptPasswordEncoder(); //реализация, использующая алгоритм
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {//определяет правила обработки HTTP-запросов и доступ к endpoint'ам.

        http
                .csrf(csrf -> csrf.disable())
                //Приложение не использует HTTP-сессии для хранения состояния авторизации. Каждый защищённый запрос должен содержать JWT-токен.
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // API үшін таза JSON жауап (401 / 403)
                //Здесь приложение возвращает понятные JSON-ответы вместо стандартной HTML-страницы ошибки.
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


                        //Здесь правила зависят от HTTP-метода и адреса:
                        .requestMatchers(
                                "/", "/books",
                                "/index.html",
                                "/login", "/register", "/cart", "/orders", "/shop", "/open-shop",
                                "/admin", "/admin/orders", "/admin/users",
                                "/details", "/details/**",
                                "/*.html",
                                "/favicon.ico", "/error",
                                "/css/**", "/js/**", "/images/**"
                        ).permitAll()

                        // Login / Register
                        .requestMatchers("/api/auth/**").permitAll()

                        // Ағымдағы пайдаланушы
                        .requestMatchers("/api/me").authenticated()

                        // Пайдаланушыларға рөл беру: тек ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Кітаптар: оқу: барлығына (ҚАТАҢ ретпен: /mine алдымен),
                        // қосу/өзгерту/өшіру/restock: ADMIN немесе SELLER
                        // (SELLER тек өз кітабын, бұл BookService-те тексеріледі)
                        .requestMatchers(HttpMethod.GET, "/api/books/mine").hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers(HttpMethod.GET, "/api/books", "/api/books/**").permitAll()
                        .requestMatchers("/api/books/**").hasAnyRole("ADMIN", "SELLER")

                        // Дүкендер: көру: барлығына, ашу/өзгерту: тек қызметкер (SELLER) немесе ADMIN
                        // (/mine алдымен тұруы керек)
                        .requestMatchers(HttpMethod.GET, "/api/shops/mine").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/shops", "/api/shops/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/shops").hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers(HttpMethod.PUT, "/api/shops/mine").hasAnyRole("ADMIN", "SELLER")

                        // Категориялар: оқу: барлығына, өзгерту: ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/categories", "/api/categories/**").permitAll()
                        .requestMatchers("/api/categories/**").hasRole("ADMIN")

                        // Себет: тек кірген пайдаланушы
                        .requestMatchers("/api/cart", "/api/cart/**").authenticated()

                        // Тапсырыс жасау және өз тапсырыстарын көру: кірген пайдаланушы
                        .requestMatchers(HttpMethod.POST, "/api/orders").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/orders/my").authenticated()

                        // Қалған тапсырыс операциялары: ADMIN
                        .requestMatchers("/api/orders", "/api/orders/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}