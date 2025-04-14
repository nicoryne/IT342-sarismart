package me.sarismart.backend.Service;

import me.sarismart.backend.Entity.User;
import me.sarismart.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User saveUserToDatabase(String email, String supabaseUid, String fullName) {
        if (existsByEmail(email)) {
            return userRepository.findByEmail(email);
        }

        User user = new User();
        user.setEmail(email);
        user.setSupabaseUid(supabaseUid);
        user.setFullName(fullName);

        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserBySupabaseUid(String uid) {
        return userRepository.findBySupabaseUid(uid);
    }
}
