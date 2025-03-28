package com.mypetserver.mypetserver.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * This class defines the owner interfacing entity for the owner in a database repository
 */
@Getter
@Setter
@Entity
@Table(name = "owners")
public class Owner {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "displayname")
    private String displayName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

}
