package com.jw.account.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank(message = "You must provide a username.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username can only contain letters and numbers.")
    private String username;

    @NotBlank(message = "You must provide a password.")
    private String password;

    private String email;
}
