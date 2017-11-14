package com.example.assess2.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.assess2.exceptions.TagDoesNotExistException;
import com.example.assess2.exceptions.UserDoesNotExistException;
import com.example.assess2.mappers.TweetMapper;
import com.example.assess2.objects.Credentials;
import com.example.assess2.objects.Hashtag;
import com.example.assess2.objects.Tweet;
import com.example.assess2.objects.User;
import com.example.assess2.objectsdto.TweetDto;
import com.example.assess2.repositories.HashtagRepository;
import com.example.assess2.repositories.TweetRepository;

@Service
public class TweetService {

	private TweetRepository tweetRepo;
	private UserService userService;
	private ValidationService validationService;
	private TweetMapper mapper;
	private HashtagRepository hashtagRepo;

	public TweetService(TweetRepository tweetRepo, UserService userService, HashtagRepository hashtagRepo,
			ValidationService validationService, TweetMapper mapper) {
		this.tweetRepo = tweetRepo;
		this.userService = userService;
		this.validationService = validationService;
		this.hashtagRepo = hashtagRepo;
		this.mapper = mapper;
	}

	public List<TweetDto> getAll() {
		return tweetRepo.findAll().stream().sorted((a, b) -> {
			if (a.getPosted().after(b.getPosted()))
				return 0;
			else
				return 1;
		}).map(mapper::toDto).collect(Collectors.toList());
	}

	public List<TweetDto> getFeed(String username) throws UserDoesNotExistException {
		if (validationService.userExistsAndActive(username)) {
			List<Tweet> tweets = null;

			User user = userService.getUserByUsername(username);
			List<User> following = user.getFollowing();

			tweets = tweetRepo.findByAuthorAndDeletedIsFalse(user);
			for (User u : following) {
				tweets.addAll(tweetRepo.findByAuthorAndDeletedIsFalse(u));
			}
			return tweets.stream().sorted((a, b) -> {
				if (a.getPosted().after(b.getPosted()))
					return 0;
				else
					return 1;
			}).map(mapper::toDto).collect(Collectors.toList());
		} else {
			throw new UserDoesNotExistException();
		}
	}

	public List<TweetDto> getTweets(String username) throws UserDoesNotExistException {
		if (validationService.userExistsAndActive(username)) {
			List<Tweet> tweets = null;

			tweets = tweetRepo.findByAuthorAndDeletedIsFalse(userService.getUserByUsername(username));
			return tweets.stream().sorted((a, b) -> {
				if (a.getPosted().after(b.getPosted()))
					return 0;
				else
					return 1;
			}).map(mapper::toDto).collect(Collectors.toList());
		} else {
			throw new UserDoesNotExistException();
		}
	}

	public List<TweetDto> getMentions(String username) throws UserDoesNotExistException {
		if (validationService.userExistsAndActive(username)) {
			List<Tweet> tweets = null;

			tweets = tweetRepo.findByMentionsAndDeletedIsFalse(userService.getUserByUsername(username));
			return tweets.stream().sorted((a, b) -> {
				if (a.getPosted().after(b.getPosted()))
					return 0;
				else
					return 1;
			}).map(mapper::toDto).collect(Collectors.toList());
		} else {
			throw new UserDoesNotExistException();
		}
	}

	public List<TweetDto> getTweetsByTag(Hashtag tag) {
		return tweetRepo.findByTagsAndDeletedIsFalse(tag).stream().map(mapper::toDto).collect(Collectors.toList());
	}

	public TweetDto getTweetById(Integer id) throws TagDoesNotExistException {
		if (tweetRepo.existsByIdAndDeletedIsFalse(id)) {
			return mapper.toDto(tweetRepo.findByIdAndDeletedIsFalse(id));
		} else {
			throw new TagDoesNotExistException();
		}
	}

	public TweetDto postTweet(String content, Credentials credentials) throws UserDoesNotExistException {
		if (validationService.userExistsAndActive(credentials.getUsername())) {

			Tweet tweet = new Tweet(null, userService.getUserByUsername(credentials.getUsername()),
					System.currentTimeMillis(), content, null, null);
			List<Hashtag> tags = new ArrayList<Hashtag>();
			List<User> mentions = new ArrayList<User>();

			postTweetGetMentionsAndTags(tags, mentions, content);
			tweet.setMentions(mentions);
			tweet.setTags(tags);
			tweet = tweetRepo.save(tweet);

			for (User u : tweet.getMentions()) {
				u.getMentions().add(tweet);
			}

			return mapper.toDto(tweet);
		} else {
			throw new UserDoesNotExistException();
		}
	}

	public void postTweetGetMentionsAndTags(List<Hashtag> tags, List<User> mentions, String content) {
		Long time = System.currentTimeMillis();
		String extractedString = "";
		Boolean tag = false;
		Boolean mention = false;
		if (content.charAt(0) == '#')
			tag = true;
		if (content.charAt(0) == '@')
			mention = true;
		String modifiedContent = content.substring(1);
		for (char c : modifiedContent.toCharArray()) {
			if (c == '#') {
				if (tag) {
					if (!hashtagRepo.existsByLabel(extractedString)) {
						Hashtag hashtag = new Hashtag(extractedString, time);
						hashtagRepo.save(hashtag);
					}
					Hashtag hashtag = hashtagRepo.findByLabel(extractedString);
					hashtag.setLastUsed(time);
					tags.add(hashtag);
					extractedString = "";
				}
				if (mention) {
					if (!validationService.userExists(extractedString)) {
						try {
							mentions.add(userService.getUserByUsername(extractedString));
						} catch (UserDoesNotExistException e) {
							e.printStackTrace();
						}
					}
					extractedString = "";
				}
				tag = true;
				mention = false;
				continue;
			}
			if (c == '@') {
				if (tag) {
					if (!hashtagRepo.existsByLabel(extractedString)) {
						Hashtag hashtag = new Hashtag(extractedString, time);
						hashtagRepo.save(hashtag);
					}
					Hashtag hashtag = hashtagRepo.findByLabel(extractedString);
					hashtag.setLastUsed(time);
					tags.add(hashtag);
					extractedString = "";
				}
				if (mention) {
					if (!validationService.userExists(extractedString)) {
						try {
							mentions.add(userService.getUserByUsername(extractedString));
						} catch (UserDoesNotExistException e) {
							e.printStackTrace();
						}
					}
					extractedString = "";
				}
				mention = true;
				tag = false;
				continue;
			}
			if (Character.isWhitespace(c)) {
				if (tag) {
					if (!hashtagRepo.existsByLabel(extractedString)) {
						Hashtag hashtag = new Hashtag(extractedString, time);
						hashtagRepo.save(hashtag);
					}
					Hashtag hashtag = hashtagRepo.findByLabel(extractedString);
					hashtag.setLastUsed(time);
					tags.add(hashtag);
					extractedString = "";
				}
				if (mention) {
					if (!validationService.userExists(extractedString)) {
						try {
							mentions.add(userService.getUserByUsername(extractedString));
						} catch (UserDoesNotExistException e) {
							e.printStackTrace();
						}
					}
					extractedString = "";
				}
				mention = false;
				tag = false;
				continue;
			}
			if (tag || mention) {
				extractedString = extractedString + c;
			}
		}
		if (tag) {
			if (!hashtagRepo.existsByLabel(extractedString)) {
				Hashtag hashtag = new Hashtag(extractedString, time);
				hashtagRepo.save(hashtag);
			}
			Hashtag hashtag = hashtagRepo.findByLabel(extractedString);
			hashtag.setLastUsed(time);
			tags.add(hashtag);
		}
		if (mention) {
			if (!hashtagRepo.existsByLabel(extractedString)) {
				Hashtag hashtag = new Hashtag(extractedString, time);
				hashtagRepo.save(hashtag);
			}
			Hashtag hashtag = hashtagRepo.findByLabel(extractedString);
			hashtag.setLastUsed(time);
			tags.add(hashtag);
		}

	}

}
