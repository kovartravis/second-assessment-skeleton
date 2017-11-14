package com.example.assess2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.assess2.objects.Hashtag;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {
    
	public List<Hashtag> findAll();
	public Boolean existsByLabel(String label);
	public Hashtag findByLabel(String label);
	@SuppressWarnings("unchecked")
	public Hashtag save(Hashtag tag);
}
