package com.example.assess2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.assess2.objectsdto.TweetDto;

public interface TweetDtoRepository extends JpaRepository<TweetDto, Integer>{

	@SuppressWarnings("unchecked")
	public TweetDto save (TweetDto dto);
}
