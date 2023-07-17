package com.jw.accounts.services;
import com.jw.accounts.entities.Account;
import com.jw.accounts.exceptions.UserNotFoundException;
import com.jw.accounts.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    private static final String ACCOUNT_ID_NOT_FOUND = "Account not found with id: ";


    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Page<Account> getAllAccounts(int page, int size) {


        Pageable pageable = PageRequest.of(page, size);

        return accountRepository.findAll(pageable);
    }

    public Account getAccountByNumberOrName(String accountNumber, String accountName) {
        if (accountNumber != null) {
            return accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new UserNotFoundException("Account not found with account number: " + accountNumber));
        } else if (accountName != null) {
            return accountRepository.findByName(accountName)
                    .orElseThrow(() -> new UserNotFoundException("Account not found with account name: " + accountName));
        } else {
            throw new IllegalArgumentException("Either accountNumber or accountName must be provided.");
        }
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(ACCOUNT_ID_NOT_FOUND + id));
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, Account accountDetails) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(ACCOUNT_ID_NOT_FOUND + id));

        account.setName(accountDetails.getName());
        account.setEmail(accountDetails.getEmail());
        account.setAccountNumber(accountDetails.getAccountNumber());

        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(ACCOUNT_ID_NOT_FOUND + id));

        accountRepository.delete(account);
    }
}
