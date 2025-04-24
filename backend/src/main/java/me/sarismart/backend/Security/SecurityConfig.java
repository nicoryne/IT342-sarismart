package me.sarismart.backend.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
<<<<<<< HEAD
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Public access for Swagger UI
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()

                // Public access for authentication endpoints
                .requestMatchers(
                    "/api/v1/auth/test",
                    "/api/v1/auth/register",
                    "/api/v1/auth/login",
                    "/api/v1/auth/refresh"
                ).permitAll()

                // Authenticated access for user details
                .requestMatchers(
                    "/api/v1/auth/user"
                ).hasAuthority("authenticated")

                // Store-related endpoints
                .requestMatchers(HttpMethod.GET, "/api/v1/stores").permitAll() // Get all stores
                .requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}").permitAll() // Get store by ID
                .requestMatchers(HttpMethod.POST, "/api/v1/stores").hasAuthority("authenticated") // Create a store
                .requestMatchers(HttpMethod.PUT, "/api/v1/stores/{storeId}").hasAuthority("authenticated") // Update a store
                .requestMatchers(HttpMethod.DELETE, "/api/v1/stores/{storeId}").hasAuthority("authenticated") // Delete a store

                // Worker-related endpoints
                .requestMatchers(HttpMethod.POST, "/api/v1/stores/{storeId}/workers/{workerId}").hasAuthority("authenticated") // Assign a worker
                .requestMatchers(HttpMethod.DELETE, "/api/v1/stores/{storeId}/workers/{workerId}").hasAuthority("authenticated") // Remove a worker
                .requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}/workers").permitAll() // List workers in a store

                // Product-related endpoints
                .requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}/products").permitAll() // List products in a store
                .requestMatchers(HttpMethod.POST, "/api/v1/stores/{storeId}/products").hasAuthority("authenticated") // Create a product
                .requestMatchers(HttpMethod.PUT, "/api/v1/stores/{storeId}/products/{productId}").hasAuthority("authenticated") // Modify a product
                .requestMatchers(HttpMethod.PUT, "/api/v1/stores/{storeId}/owner/products/{productId}").hasAuthority("authenticated") // Modify a product by owner
                .requestMatchers(HttpMethod.DELETE, "/api/v1/stores/{storeId}/products/{productId}").hasAuthority("authenticated") // Delete a product

                // Stock adjustment endpoints
                .requestMatchers(HttpMethod.PATCH, "/api/v1/stores/{storeId}/products/{productId}/stock").hasAuthority("authenticated") // Adjust product stock
                .requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}/stock/history").hasAuthority("authenticated") // Get stock adjustment history for a store
                .requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}/products/{productId}/stock/history").hasAuthority("authenticated") // Get stock adjustment history for a product

                // Sales-related endpoints
                .requestMatchers(HttpMethod.POST, "/api/v1/stores/{storeId}/transactions/sales").hasAuthority("authenticated") // Create a sale
                .requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}/transactions/sales/{saleId}").hasAuthority("authenticated") // Get a sale by ID
                .requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}/transactions/sales").hasAuthority("authenticated") // List sales
                .requestMatchers(HttpMethod.DELETE, "/api/v1/stores/{storeId}/transactions/sales/{saleId}").hasAuthority("authenticated") // Refund a sale

                // Inventory-related endpoints
                .requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}/inventory/alerts").hasAuthority("authenticated") // Get restock alerts
                .requestMatchers(HttpMethod.PUT, "/api/v1/stores/{storeId}/inventory/{productId}/reorder").hasAuthority("authenticated") // Set reorder level

                // Report-related endpoints
                .requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}/reports/daily").hasAuthority("authenticated") // Get daily sales report
                .requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}/reports/monthly").hasAuthority("authenticated") // Get monthly sales report
                .requestMatchers(HttpMethod.GET, "/api/v1/stores/{storeId}/reports/inventory").hasAuthority("authenticated") // Get inventory status report

                // Default rule: All other requests require authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(Customizer.withDefaults());
=======
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/v1/stores/**")
                        .permitAll()
                        .requestMatchers(
                                "/api/v1/products/**")
                        .hasAuthority("authenticated")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());
>>>>>>> dev

        return http.build();
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}