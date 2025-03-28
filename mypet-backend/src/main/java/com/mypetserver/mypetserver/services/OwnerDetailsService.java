package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.dto.Owner;
import com.mypetserver.mypetserver.dto.User;
import com.mypetserver.mypetserver.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * This class defines a detail service for use with Spring Security authentication.
 * This class helps bridge the User class and the Owner class for authentication purposes.
 */
@Service
public class OwnerDetailsService implements UserDetailsService {
    private final OwnerRepository ownerRepository;

    @Autowired
    public OwnerDetailsService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Owner owner = ownerRepository.getOwnerByUsername(username);
        if (owner == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new User(owner.getUsername(), owner.getPassword());
    }
}
