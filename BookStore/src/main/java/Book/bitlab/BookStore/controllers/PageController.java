package Book.bitlab.BookStore.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }

    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }

    @GetMapping("/cart")
    public String cart() {
        return "forward:/cart.html";
    }

    @GetMapping("/admin")
    public String admin() {
        return "forward:/admin.html";
    }

    @GetMapping("/details")
    public String details() {
        return "forward:/details.html";
    }
}