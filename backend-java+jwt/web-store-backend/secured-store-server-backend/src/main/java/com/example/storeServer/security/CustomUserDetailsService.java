package com.example.storeServer.security;

import com.example.storeServer.model.CustomUser;
import com.example.storeServer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUser user = userService.getUserByUsernameHelper(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Log the retrieved user
        System.out.println("Retrieved user: " + user.getUserName() + " with roles: " + user.getRole().name());

        // Convert user roles to GrantedAuthorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().name())); // Assuming role is enum

        // Return user details with authorities
        return new User(user.getUserName(), user.getPassword(), authorities);
    }

}



