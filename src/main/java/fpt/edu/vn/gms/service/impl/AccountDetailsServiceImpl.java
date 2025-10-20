package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.Account;
import fpt.edu.vn.gms.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .roles(account.getRole().getName())
                .disabled(!account.isActive())
                .build();
    }
}
