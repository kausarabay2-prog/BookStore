package Book.bitlab.BookStore;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Связываем все URL-адреса напрямую с index.html без редиректов
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/books").setViewName("forward:/index.html");
        registry.addViewController("/cart").setViewName("forward:/index.html");
        registry.addViewController("/login").setViewName("forward:/index.html");
        registry.addViewController("/register").setViewName("forward:/index.html");
    }
}