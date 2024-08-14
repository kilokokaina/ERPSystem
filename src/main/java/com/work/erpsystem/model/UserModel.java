package com.work.erpsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@Entity
@NoArgsConstructor
public class UserModel implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    private String username;
    private String password;

    private String firstName;
    private String secondName;
    private String post;

    private String userUUID = UUID.randomUUID().toString();

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roleSet;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "org_role",
            joinColumns = { @JoinColumn(name = "user_id") })
    @MapKeyJoinColumn(name = "org")
    private Map<OrganizationModel, String> orgRole;

    @OneToOne
    @JoinTable(
            name = "user_avatar", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private FileModel userAvatar;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoleSet();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return this.getUsername();
    }
}
