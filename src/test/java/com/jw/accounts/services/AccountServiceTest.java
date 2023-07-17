package com.jw.accounts.services;

import com.jw.accounts.entities.Account;
import com.jw.accounts.exceptions.UserNotFoundException;
import com.jw.accounts.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllAccounts() {
        // Prepare test data
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1L, "John Doe", "john@example.com", "1234567890"));
        accounts.add(new Account(2L, "Jane Smith", "jane@example.com", "0987654321"));
        Page<Account> accountPage = new PageImpl<>(accounts);

        // Mock the repository method
        when(accountRepository.findAll(any(Pageable.class))).thenReturn(accountPage);

        // Call the service method
        Page<Account> result = accountService.getAllAccounts(0, 10);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(accounts, result.getContent());
    }

    @Test
    void testGetAccountByNumberOrNameWithValidAccountNumber() {
        // Prepare test data
        Account account = new Account(1L, "John Doe", "john@example.com", "1234567890");

        // Mock the repository method
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        // Call the service method
        Account result = accountService.getAccountByNumberOrName("1234567890", null);

        // Assertions
        assertNotNull(result);
        assertEquals(account, result);
    }

    @Test
    void testGetAccountByNumberOrNameWithValidAccountName() {
        // Prepare test data
        Account account = new Account(1L, "John Doe", "john@example.com", "1234567890");

        // Mock the repository method
        when(accountRepository.findByName(anyString())).thenReturn(Optional.of(account));

        // Call the service method
        Account result = accountService.getAccountByNumberOrName(null, "John Doe");

        // Assertions
        assertNotNull(result);
        assertEquals(account, result);
    }

    @Test
    void testGetAccountByNumberOrNameWithInvalidInput() {
        // Assertions
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountByNumberOrName(null, null));
    }

    @Test
    void testGetAccountByNumberOrNameWithNonExistentAccountNumber() {
        // Mock the repository method
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        // Assertions
        assertThrows(UserNotFoundException.class, () -> accountService.getAccountByNumberOrName("1234567890", null));
    }

    @Test
    void testGetAccountByNumberOrNameWithNonExistentAccountName() {
        // Mock the repository method
        when(accountRepository.findByName(anyString())).thenReturn(Optional.empty());

        // Assertions
        assertThrows(UserNotFoundException.class, () -> accountService.getAccountByNumberOrName(null, "John Doe"));
    }

    @Test
    void testGetAccountByIdWithValidId() {
        // Prepare test data
        Account account = new Account(1L, "John Doe", "john@example.com", "1234567890");

        // Mock the repository method
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        // Call the service method
        Account result = accountService.getAccountById(1L);

        // Assertions
        assertNotNull(result);
        assertEquals(account, result);
    }

    @Test
    void testGetAccountByIdWithNonExistentId() {
        // Mock the repository method
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Assertions
        assertThrows(UserNotFoundException.class, () -> accountService.getAccountById(1L));
    }

    @Test
    void testCreateAccount() {
        // Prepare test data
        Account account = new Account(1L, "John Doe", "john@example.com", "1234567890");

        // Mock the repository method
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Call the service method
        Account result = accountService.createAccount(account);

        // Assertions
        assertNotNull(result);
        assertEquals(account, result);
    }

    @Test
    void testUpdateAccountWithValidId() {
        // Prepare test data
        Account existingAccount = new Account(1L, "John Doe", "john@example.com", "1234567890");
        Account updatedAccount = new Account(1L, "John Smith", "john@example.com", "1234567890");

        // Mock the repository methods
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        // Call the service method
        Account result = accountService.updateAccount(1L, updatedAccount);

        // Assertions
        assertNotNull(result);
        assertEquals(updatedAccount, result);
    }

    @Test
    void testUpdateAccountWithNonExistentId() {
        // Prepare test data
        Account updatedAccount = new Account(1L, "John Smith", "john@example.com", "1234567890");

        // Mock the repository method
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Assertions
        assertThrows(UserNotFoundException.class, () -> accountService.updateAccount(1L, updatedAccount));
    }

    @Test
    void testDeleteAccountWithValidId() {
        // Prepare test data
        Account account = new Account(1L, "John Doe", "john@example.com", "1234567890");

        // Mock the repository methods
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        // Call the service method
        assertDoesNotThrow(() -> accountService.deleteAccount(1L));
    }

    @Test
    void testDeleteAccountWithNonExistentId() {
        // Mock the repository method
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Assertions
        assertThrows(UserNotFoundException.class, () -> accountService.deleteAccount(1L));
    }
}
