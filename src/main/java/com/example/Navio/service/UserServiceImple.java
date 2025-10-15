package com.example.Navio.service;

import com.example.Navio.interfaces.UserService;
import com.example.Navio.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.Navio.model.User;
import java.util.List;

@Service
public class UserServiceImple implements UserService {

    private final UserRepository userRepository;

    public UserServiceImple(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public boolean deleteUser(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
        return true;
    }

    public User findUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}
