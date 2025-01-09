package dev.orisha.user_service.data.mappers.impls;

import dev.orisha.user_service.data.mappers.UserMapper;
import dev.orisha.user_service.data.models.User;
import dev.orisha.user_service.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapperImpl implements UserMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public UserMapperImpl(final ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDTO toDto(final User entity) {
        if (entity == null) return null;

        UserDTO userDTO = new UserDTO();

        userDTO.setId(entity.getId());
        userDTO.setFirstName(entity.getFirstName());
        userDTO.setLastName(entity.getLastName());
        userDTO.setEmail(entity.getEmail());
        userDTO.setPassword(entity.getPassword());
        userDTO.setAuthorities(entity.getAuthorities());
        userDTO.setDateRegistered(entity.getDateRegistered());
        userDTO.setDateUpdated(entity.getDateUpdated());

        return userDTO;
    }

    @Override
    public User toEntity(final UserDTO dto) {
        if (dto == null) return null;

        User user = new User();

        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setAuthorities(dto.getAuthorities());

        return user;

    }

    @Override
    public List<UserDTO> toDtoList(List<User> entityList) {
        if (entityList == null) return null;
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<User> toEntityList(List<UserDTO> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void partialUpdate(User entity, UserDTO dto) {
        if (dto != null) {
            modelMapper.map(dto, entity);
        }
    }
}
