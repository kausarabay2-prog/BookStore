package Book.bitlab.BookStore.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // index.html ішіндегі SPA осы маршруттарды өзі ашады,
    // сондықтан беттің жаңартылуында (F5) 404 болмайды.
    @GetMapping({"/", "/books", "/cart", "/details", "/orders", "/shop", "/open-shop"})
    public String home() {
        return "forward:/index.html";
    }

    @GetMapping({"/login", "/register"})
    public String login() {
        return "forward:/login.html";
    }

    // ADMIN: барлық кітап. SELLER: өз кітаптары.
    @GetMapping("/admin")
    public String admin() {
        return "forward:/admin.html";
    }

    @GetMapping("/admin/orders")
    public String adminOrders() {
        return "forward:/admin-orders.html";
    }

    @GetMapping("/admin/users")
    public String adminUsers() {
        return "forward:/admin-users.html";
    }
}