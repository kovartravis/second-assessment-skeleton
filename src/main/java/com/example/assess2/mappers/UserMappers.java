package com.example.assess2.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.assess2.objects.User;
import com.example.assess2.objectsdto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMappers {
	
	UserMappers instance = Mappers.getMapper(UserMappers.class);
	@Mapping(source = "credentials.username", target = "username")
	UserDto toDto(User user);
	
	@Mapping(source = "username", target = "credentials.username")
	User toUser(UserDto dto);

}
