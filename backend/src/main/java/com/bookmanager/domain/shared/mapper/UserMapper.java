package com.bookmanager.domain.shared.mapper;

import com.bookmanager.domain.auth.dto.UserResponse;
import com.bookmanager.domain.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
