package com.jw.accounts.generators;

import com.jw.accounts.repositories.RolesRepository;
import com.jw.accounts.repositories.UsersRepository;
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