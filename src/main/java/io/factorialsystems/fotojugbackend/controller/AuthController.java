package io.factorialsystems.fotojugbackend.controller;

import io.factorialsystems.fotojugbackend.model.auth.Role;
import io.factorialsystems.fotojugbackend.model.auth.RoleType;
import io.factorialsystems.fotojugbackend.model.auth.User;
import io.factorialsystems.fotojugbackend.payload.request.LoginRequest;
import io.factorialsystems.fotojugbackend.payload.request.SignupRequest;
import io.factorialsystems.fotojugbackend.payload.response.JwtResponse;
import io.factorialsystems.fotojugbackend.payload.response.MessageResponse;
import io.factorialsystems.fotojugbackend.repository.RoleRepository;
import io.factorialsystems.fotojugbackend.repository.UserRepository;
import io.factorialsystems.fotojugbackend.security.JwtUtils;
import io.factorialsystems.fotojugbackend.service.auth.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error E-mail is already in use!"));
        }

        // Create User Account
        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleType(RoleType.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error Role USER is not Found!!!"));

            roles.add(userRole);
            log.info("Roles NULL, Added user Role");
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleType(RoleType.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error Role ADMIN Not Found"));
                        roles.add(adminRole);
                        log.info("Added Admin Role");
                        break;

                    case "mod":
                        Role modRole = roleRepository.findByRoleType(RoleType.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error Role MODERATOR Not Found"));
                        roles.add(modRole);
                        log.info("Added Moderator Role");
                        break;

                    case "user":
                        Role userRole = roleRepository.findByRoleType(RoleType.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error Role USER is not Found!!!"));

                        roles.add(userRole);
                        log.info("Added User Role");
                        break;

                    default:
                        String errorMessage = String.format("Unknown Role Encountered : %s", role);
                        log.error(errorMessage);
                        throw new RuntimeException(errorMessage);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User Registered Successfully"));
    }


}
