package com.example.ERS.service;

import com.example.ERS.entity.User;
import com.example.ERS.entity.Reimbursement;
import com.example.ERS.entity.Role;
import com.example.ERS.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.example.ERS.repository.ReimbursementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

@Service
public class UserService {

    @Autowired
    @Lazy
    UserRepository userRepository;

    @Autowired
    @Lazy
    ReimbursementRepository reimbursementRepository;

    @Autowired
    @Lazy
    JwtService jwtService;
    
    @Transactional
    public User registerUser(User user) { // add duplicate username exception
        if(user.getUsername() == "" || user.getPassword().length() < 4 || 
        userRepository.findUserByUsername(user.getUsername()) != null) {
            return null;
        }
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        user.setRoleId(new Role("Employee"));
        User fin = userRepository.save(user);
        userRepository.flush();
        return fin;
    }

    public String loginUser(String username, String password) throws JsonProcessingException {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findUserByUsername(username));
        if(userOptional.isPresent() && BCrypt.checkpw(password, userOptional.get().getPassword())) {
            User user = userOptional.get();
            String jwt = jwtService.generateToken(user); //this call doesnt work
            return jwt; //json this or in controller
        }
        return null;
    }

    public List<User> getAllUsers(String token) {
        Role role = jwtService.getRoleFromToken(token);

        if(role.getRole().equals("Manager")) {
            return new ArrayList<User>(userRepository.findAll());
        }
        return null;
    }

    public User deleteUser(Integer id, String token) {

        Role role = jwtService.getRoleFromToken(token);
        Optional<User> userOptional = userRepository.findById(id);
        
        if(userOptional.isPresent() && role.getRole().equals("Manager")) {
            User user = userOptional.get();
            List<Reimbursement> reimbursements = user.getReimbursements();
            for(Reimbursement reimbursement : reimbursements) {
                reimbursementRepository.delete(reimbursement);
            }
            userRepository.delete(user);
            return user;
        }
        return null;
    }
    
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
