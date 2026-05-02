package com.epti.backend.service;

import com.epti.backend.dto.auth.LoginRequest;
import com.epti.backend.dto.auth.LoginResponse;
import com.epti.backend.dto.auth.RegisterRequest;
import com.epti.backend.exception.BadRequestException;
import com.epti.backend.exception.ResourceNotFoundException;
import com.epti.backend.model.Order;
import com.epti.backend.model.Role;
import com.epti.backend.model.User;
import com.epti.backend.model.enums.Turma;
import com.epti.backend.repository.OrderRepository;
import com.epti.backend.repository.RoleRepository;
import com.epti.backend.repository.UserRepository;
import com.epti.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditService auditService;

    // Pattern for institutional email: nome.sobrenomeNumero@aluno.ce.gov.br
    private static final Pattern INSTITUTIONAL_EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z]+\\.[a-zA-Z]+\\d+@aluno\\.ce\\.gov\\.br$");

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(getAuthorities(user.getRoles()))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", loginRequest.getUsername()));

        String token = jwtTokenProvider.generateToken(authentication);

        auditService.logLoginAttempt(loginRequest.getUsername(), true, "unknown");

        return LoginResponse.builder()
                .token(token)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public User register(RegisterRequest registerRequest) {
        log.info("Registering new user: {}", registerRequest.getUsername());

        // Validate email format
        validateEmail(registerRequest.getEmail());

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "USER"));
        roles.add(userRole);

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .turma(registerRequest.getTurma())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .enabled(true)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);
        auditService.logAction("USER_REGISTERED", savedUser, "New user registered with turma " + registerRequest.getTurma());

        return savedUser;
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        // Check if it's institutional email
        if (isInstitutionalEmail(email)) {
            if (!INSTITUTIONAL_EMAIL_PATTERN.matcher(email).matches()) {
                throw new BadRequestException("Invalid institutional email format. Expected format: nome.sobrenomeNumero@aluno.ce.gov.br");
            }
        } else {
            // Validate regular email format
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
                throw new BadRequestException("Invalid email format");
            }
        }
    }

    private boolean isInstitutionalEmail(String email) {
        return email.endsWith("@aluno.ce.gov.br");
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User is not authenticated");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private String[] getAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> "ROLE_" + role.getName())
                .toArray(String[]::new);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByTurma(Turma turma) {
        return userRepository.findByTurma(turma);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByPaymentStatus(boolean hasPaid) {
        return userRepository.findByHasPaid(hasPaid);
    }

    @Transactional(readOnly = true)
    public List<User> getUnpaidUsersFromParticipatingTurmas() {
        return userRepository.findByHasPaidFalseAndTurmaIn(
                Turma.PRIMEIRO_D,
                Turma.SEGUNDO_A,
                Turma.SEGUNDO_D,
                Turma.SEGUNDO_D_MARTA
        );
    }

    public void markUserAsPaid(Long userId) {
        User user = getUserById(userId);
        user.setHasPaid(true);
        updateUser(user);
        auditService.logAction("USER_MARKED_PAID", user, "User marked as paid");
    }

    @Transactional(readOnly = true)
    public boolean isUserInParticipatingTurma(User user) {
        return user.isParticipatingTurma();
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean isOrderOwner(Long orderId, String username) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            return order != null && order.getUser().getUsername().equals(username);
        } catch (Exception e) {
            log.error("Error checking order ownership", e);
            return false;
        }
    }
}
