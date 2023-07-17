package com.jw.account.generators;

import com.jw.account.repositories.RolesRepository;
import com.jw.account.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class UsersGenerator {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private RolesRepository rolesRepository;

    public void generateUsers(){
        // TODO document why this method is empty
    }
}