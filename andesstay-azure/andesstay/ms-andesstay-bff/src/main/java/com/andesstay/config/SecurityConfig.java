package com.andesstay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Seguridad centralizada del BFF — es el ÚNICO servicio expuesto
 * públicamente. Valida el JWT emitido directamente por Azure AD
 * (App Registration "BarrioDigital", login con MSAL desde el frontend) y,
 * una vez autenticado, reenvía la request al microservicio de dominio
 * correspondiente (ver ProxyController). Los microservicios de dominio NO
 * exponen seguridad propia: solo son accesibles dentro de la red interna.
 *
 * Los roles vienen en el claim "roles" del token (App Roles de Azure AD:
 * Admin, Recepcionista/Operador, Huesped/Cliente, Auditor).
 *
 * TOGGLE: mientras `andesstay.security.enabled=false` (default), todos los
 * endpoints quedan abiertos. Para activarla, variables de entorno:
 *   1) ANDESSTAY_SECURITY_ENABLED=true
 *   2) SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=
 *        https://login.microsoftonline.com/<TENANT_ID>/v2.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${andesstay.security.enabled:false}")
    private boolean securityEnabled;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        if (!securityEnabled) {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/catalog/units").authenticated()
                        .requestMatchers("/api/catalog/**").hasAnyRole("Admin", "admin")
                        .requestMatchers("/api/report/**").hasAnyRole("Admin", "admin")
                        .requestMatchers("/api/audit/**").hasAnyRole("Admin", "admin", "Auditor")
                        .requestMatchers("/api/reservations/**").hasAnyRole("Admin", "admin", "Recepcionista", "Operador", "Huesped", "Cliente")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /** Convierte el claim "roles" de Azure AD (lista de App Roles) en authorities ROLE_xxx. */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> scopeAuthorities = scopesConverter.convert(jwt);
            List<GrantedAuthority> result = new ArrayList<>(scopeAuthorities == null ? List.of() : scopeAuthorities);

            List<String> appRoles = jwt.getClaimAsStringList("roles");
            if (appRoles != null) {
                appRoles.forEach(r -> result.add(new SimpleGrantedAuthority("ROLE_" + r)));
            }
            return result.stream().distinct().collect(Collectors.toList());
        });
        return converter;
    }
}
