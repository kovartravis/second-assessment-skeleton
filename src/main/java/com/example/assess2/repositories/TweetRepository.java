package com.example.assess2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.assess2.objects.Hashtag;
import com.example.assess2.objects.Tweet;
import com.example.assess2.objects.User;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Integer>{

	public List<Tweet> findByAuthorAndDeletedIsFalse(User user);
	public List<Tweet> findByAuthor(User user);
	public List<Tweet> findByMentionsAndDeletedIsFalse(User user);
	public List<Tweet> findByTagsAndDeletedIsFalse(Hashtag tag);
	public Boolean existsById(Integer primaryKey);
	public Boolean existsByIdAndDeletedIsFalse(Integer primaryKey);
	public Tweet findByIdAndDeletedIsFalse(Integer primaryKey);
	public List<Tweet> findByDeletedIsFalse();
	public List<Tweet> findByInRepostOfId(Integer id);
	public List<Tweet> findByInReplyToId(Integer id);
	@SuppressWarnings("unchecked")
	public Tweet save(Tweet tweet);
}
