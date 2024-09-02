package com.app.auth.service.impl.auth;

import com.app.auth.model.entity.User;
import com.app.auth.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final IUserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException("User not found"));

    Collection<GrantedAuthority> authorities = new ArrayList<>();

    authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    user.getRole().getPermissions().forEach(
            permission -> authorities.add(new SimpleGrantedAuthority(permission.name())));

    return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            authorities);
  }
}
