package com.simple.identity.security;

import com.simple.identity.entity.User;
import com.simple.identity.exception.InvalidTokenException;
import com.simple.identity.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException {

        try {
            String token = resolveToken(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            authenticate(token, request);
            filterChain.doFilter(request, response);

        } catch (InvalidTokenException ex) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
        } catch (Exception ex) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return header.substring(BEARER_PREFIX.length());
    }

    private void authenticate(String token, HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        String email = jwtUtil.extractUsername(token);
        if (email == null) {
            throw new InvalidTokenException("Invalid token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("User not found"));

        if (!token.equals(user.getAuthToken()) || !jwtUtil.isTokenValid(token, user)) {
            throw new InvalidTokenException("Token invalid or expired");
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void writeError(HttpServletResponse response, int status, String message)
            throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(
                String.format("{\"status\":%d,\"message\":\"%s\"}", status, message)
        );
    }
}

