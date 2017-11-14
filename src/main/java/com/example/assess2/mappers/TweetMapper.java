package com.example.assess2.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.assess2.objects.Tweet;
import com.example.assess2.objectsdto.TweetDto;

@Mapper(componentModel = "spring")
public interface TweetMapper {
	
	@Mapping(target = "author", expression = "java(UserMappers.instance.toDto(tweet.getAuthor()))")
	TweetDto toDto(Tweet tweet);
	
	@Mapping(target = "author", expression = "java(UserMappers.instance.toUser(dto.getAuthor()))")
	Tweet toTweet(TweetDto dto);

}
