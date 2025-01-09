package dev.orisha.user_service.services;

import dev.orisha.user_service.dto.UserDTO;
import dev.orisha.user_service.dto.requests.UserUpdateRequest;

import java.util.List;

public interface UserService {

    UserDTO update(UserUpdateRequest request);

    UserDTO getUserDTO(String email);

    List<UserDTO> getAllUsers();

}
