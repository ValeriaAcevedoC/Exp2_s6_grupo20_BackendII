package com.minimarket.security.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 60000L);
        userDetails = new User(
                "admin",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))
        );
    }

    @Test
    void generateToken_debePermitirExtraerUsernameYValidarToken() {
        String token = jwtUtil.generateToken(userDetails);

        assertEquals("admin", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_cuandoUsuarioNoCoincide_debeRetornarFalse() {
        String token = jwtUtil.generateToken(userDetails);
        UserDetails otroUsuario = new User("otro", "password", List.of());

        assertFalse(jwtUtil.validateToken(token, otroUsuario));
    }

    @Test
    void validateToken_cuandoTokenEstaExpirado_debeRetornarFalse() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L);
        String token = jwtUtil.generateToken(userDetails);

        assertFalse(jwtUtil.validateToken(token, userDetails));
    }
}
