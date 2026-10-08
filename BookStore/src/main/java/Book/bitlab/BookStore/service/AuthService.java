package Book.bitlab.BookStore.service;

import Book.bitlab.BookStore.Entity.User;
import Book.bitlab.BookStore.dto.RegestrationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final  UserService userService;

    public User register(RegestrationDTO dto){
        return  userService.register(dto);
    }

    public User login(String email, String password){
        return  userService.login(email,password);

    }
}
