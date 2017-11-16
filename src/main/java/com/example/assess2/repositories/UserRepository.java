package com.example.assess2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.assess2.objects.Tweet;
import com.example.assess2.objects.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	
	@SuppressWarnings("unchecked")
	public User save(User user);
	public List<User> findByActiveIsTrue();
	public Boolean existsByCredentialsUsernameAndActiveIsTrue(String username);
	public Boolean existsByCredentialsUsernameIgnoreCaseAndActiveIsTrue(String username);
	public Boolean existsByCredentialsUsernameIgnoreCase(String username);
	public Boolean existsByCredentialsUsername(String username);
	public User findById(Integer primaryKey);
	public User findByCredentialsUsername(String username);
    public List<User> findDistinctByLikesAndActiveIsTrue(Tweet tweet);
    public List<User> findByFollowingCredentialsUsername(String username);
}
