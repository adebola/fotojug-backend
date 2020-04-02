package io.factorialsystems.fotojugbackend.payload.response;

import lombok.AllArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, Integer id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
