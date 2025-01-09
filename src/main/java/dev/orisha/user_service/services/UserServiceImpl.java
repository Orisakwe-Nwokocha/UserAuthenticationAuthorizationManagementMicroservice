package dev.orisha.user_service.services;

import dev.orisha.user_service.data.mappers.UserMapper;
import dev.orisha.user_service.data.models.User;
import dev.orisha.user_service.data.repositories.UserRepository;
import dev.orisha.user_service.dto.UserDTO;
import dev.orisha.user_service.dto.requests.UserUpdateRequest;
import dev.orisha.user_service.exceptions.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository, final UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO update(UserUpdateRequest request) {
        return getUserDTOEagerly(request.getEmail())
                .map(existingUser -> {
                    log.info("Updating existing user: {}", existingUser);
                    existingUser.getAuthorities().add(request.getAuthority());
                    userMapper.partialUpdate(existingUser, request);
                    return existingUser;
                })
                .map(userRepository::save)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserDTO(String email) {
        log.info("Trying to find user by email: {}", email);

        return userRepository.findByEmail(email).map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: '%s'".formatted(email)));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Trying to fetch all users");
        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);

    }

    private Optional<User> getUserDTOEagerly(String email) {
        log.info("Trying to find user eagerly by email: {}", email);
        return userRepository.findByEmailWithEagerRelationships(email);
    }

}
