package com.minimarket.controller;

import com.minimarket.security.model.LoginRequest;
import com.minimarket.security.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticacion", description = "Endpoint para autenticacion de usuarios y emision de tokens JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesion",
            description = "Autentica un usuario con sus credenciales y devuelve un token JWT valido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticacion exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos en la solicitud"),
            @ApiResponse(responseCode = "401", description = "Credenciales invalidas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Map<String, String> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales del usuario que desea iniciar sesion",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Credenciales validas",
                                    value = """
                                            {
                                              "username": "admin",
                                              "password": "password123"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody LoginRequest request) {
        var authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        return Map.of("token", token);
    }
}
