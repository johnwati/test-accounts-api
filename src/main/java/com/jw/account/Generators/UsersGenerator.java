package com.jw.account.Generators;

import com.jw.account.Entities.Roles;
import com.jw.account.Entities.Users;
import com.jw.account.Repositories.RolesRepository;
import com.jw.account.Repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class UsersGenerator {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private RolesRepository rolesRepository;

    public void generateUsers(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Users user = new Users();
        user.setUsername("Jonas");
        user.setEmail("TestMail");
        user.setPassword(passwordEncoder.encode("Test"));
        Roles role = rolesRepository.findByName("ROLE_ADMIN");
        user.getRoles().add(role);
        Roles role1 = rolesRepository.findByName("ROLE_MEMBER");
        user.getRoles().add(role1);
        usersRepository.save(user);

        Users user1 = new Users();
        user1.setUsername("Jonas2");
        user1.setEmail("TestMail 2");
        Roles role2 = rolesRepository.findByName("ROLE_ADMIN");
        user1.getRoles().add(role2);
        user1.setPassword(passwordEncoder.encode("test"));
        usersRepository.save(user1);
    }
}