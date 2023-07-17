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

    }
}