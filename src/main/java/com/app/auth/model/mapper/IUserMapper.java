package com.app.auth.model.mapper;

import com.app.auth.model.dto.request.CreateUserRequest;
import com.app.auth.model.dto.request.UpdateUserRequest;
import com.app.auth.model.dto.response.UserResponse;
import com.app.auth.model.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface IUserMapper {
    User toEntity(CreateUserRequest request);

    UserResponse toUserResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UpdateUserRequest request, @MappingTarget User user);
}