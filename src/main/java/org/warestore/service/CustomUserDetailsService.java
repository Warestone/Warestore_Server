package org.warestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.warestore.configuration.CustomUserDetails;
import org.warestore.model.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private  UserService userService;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return CustomUserDetails.getFromUserModel((User) userService.getUserByName(username).getBody());
    }
}
