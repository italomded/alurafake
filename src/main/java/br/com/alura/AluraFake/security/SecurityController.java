package br.com.alura.AluraFake.security;

import java.util.HashMap;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class SecurityController {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public SecurityController(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("auth")
    public ResponseEntity auth(@RequestBody @Valid NewAuthDTO newAuthDTO) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(newAuthDTO.email);
        if (Objects.equals(userDetails.getPassword(), newAuthDTO.password)) {
            return ResponseEntity.ok(jwtService.buildToken(new HashMap<>(), userDetails));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
