package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.entity.Account;
import fpt.edu.vn.gms.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class AccountDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        Account account = accountRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found with phone: " + phone));

        return new org.springframework.security.core.userdetails.User(
                account.getPhone(),
                account.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(account.getRole().getValue()))
        );
    }
}
