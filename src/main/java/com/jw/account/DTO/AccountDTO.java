package com.jw.account.DTO;
import com.jw.account.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDTO {

    private String name;
    private String email;
    private String accountNumber;

    public Account toEntity() {
        Account account =  new Account();
        account.setName(this.getName());
        account.setAccountNumber(this.getAccountNumber());
        account.setEmail(this.getAccountNumber());
        return account;
    }

    // Getters and setters
}
