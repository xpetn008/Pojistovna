package org.example.models.services;

import org.example.data.entitites.UserEntity;
import org.example.models.dto.UserDTO;
import org.example.models.exceptions.DuplicateEmailException;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void create(UserDTO userDTO, boolean isAdmin) throws DuplicateEmailException;
    void delete(UserEntity userEntity);
    void changePassword(long id, String newPassword);
}
