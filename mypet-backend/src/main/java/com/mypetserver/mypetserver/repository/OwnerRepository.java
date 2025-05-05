package com.mypetserver.mypetserver.repository;

import com.mypetserver.mypetserver.entities.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * This class defines the repository class used to interface with a database
 */
public interface OwnerRepository extends JpaRepository<Owner, String> {
    Owner findOwnerByUsername(@Param("username") String username);

    Owner findOwnerByEmail(@Param("email") String email);
}
