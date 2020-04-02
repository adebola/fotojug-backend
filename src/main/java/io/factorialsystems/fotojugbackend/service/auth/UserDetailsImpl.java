package io.factorialsystems.fotojugbackend.service.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.factorialsystems.fotojugbackend.model.auth.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String username;

    private String email;


    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public static  UserDetailsImpl build (User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleType().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
    public boolean equals(Object o) {
       if (this == o) {
           return true;
       }

       if (o == null || getClass() != o.getClass()) {
           return false;
       }

       UserDetailsImpl user = (UserDetailsImpl)o;
       return Objects.equals(id, user.id);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
