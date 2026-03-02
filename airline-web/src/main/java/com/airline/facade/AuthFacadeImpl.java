package com.airline.facade;

import com.airline.entities.User;
import com.airline.exceptions.InvalidRequestException;
import com.airline.web.dtos.UserDto;
import org.mindrot.jbcrypt.BCrypt;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.UUID;

@Singleton
public class AuthFacadeImpl implements AuthFacade {

    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Override
    public UserDto register(UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            throw new InvalidRequestException("Username is required");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new InvalidRequestException("Email is required");
        }
        String rawPassword = userDto.getPassword();
        if ((rawPassword == null || rawPassword.isEmpty()) && userDto.getToken() != null && !userDto.getToken().isEmpty()) {
            rawPassword = userDto.getToken();
        }
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new InvalidRequestException("Password is required");
        }

        EntityManager em = entityManagerProvider.get();

        // Use named query to check duplicate username
        Long usernameCount = em.createNamedQuery("User.countByUsername", Long.class)
                .setParameter("username", userDto.getUsername())
                .getSingleResult();
        if (usernameCount > 0) {
            throw new InvalidRequestException("Username already exists");
        }

        // Use named query to check duplicate email
        Long emailCount = em.createNamedQuery("User.countByEmail", Long.class)
                .setParameter("email", userDto.getEmail())
                .getSingleResult();
        if (emailCount > 0) {
            throw new InvalidRequestException("Email already registered");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        user.setPasswordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
        user.setRole(userDto.getRole() != null ? userDto.getRole() : "CUSTOMER");

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        return toDto(user);
    }

    @Override
    public UserDto login(String username, String password) {
        EntityManager em = entityManagerProvider.get();

        try {
            // Use named query instead of inline JPQL
            User user = em.createNamedQuery("User.findByUsername", User.class)
                    .setParameter("username", username)
                    .getSingleResult();

            if (!BCrypt.checkpw(password, user.getPasswordHash())) {
                throw new InvalidRequestException("Invalid username or password");
            }

            UserDto dto = toDto(user);
            dto.setToken(UUID.randomUUID().toString());
            return dto;

        } catch (NoResultException e) {
            throw new InvalidRequestException("Invalid username or password");
        }
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        return dto;
    }
}
