package com.example.assess2.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.example.assess2.objects.Tweet;
import com.example.assess2.objectsdto.TweetDto;

@Mapper(componentModel = "spring")
public interface TweetMapper {
	
	TweetMapper instance = Mappers.getMapper(TweetMapper.class);
	
	@Mappings({
		@Mapping(target = "author", expression = "java(UserMappers.instance.toDto(tweet.getAuthor()))"),
	})
	TweetDto toDto(Tweet tweet);
	
	@Mappings({
		@Mapping(target = "author", expression = "java(UserMappers.instance.toUser(dto.getAuthor()))"),
	})
	Tweet toTweet(TweetDto dto);

}
