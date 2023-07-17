package com.jw.accounts.generators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

    @Component
    @Profile("dev")
    public class BaseGenerator implements CommandLineRunner {
        @Autowired
        UsersGenerator usersGenerator;

        @Autowired
        RolesGenerator rolesGenerator;

        @Override
        public void run(String... args) throws Exception {
            rolesGenerator.generateRoles();
            usersGenerator.generateUsers();
        }
}
