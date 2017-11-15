package com.example.assess2.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.assess2.exceptions.CredentialsDoNotMatchException;
import com.example.assess2.exceptions.TagDoesNotExistException;
import com.example.assess2.exceptions.TweetDoesNotExistException;
import com.example.assess2.exceptions.UserDoesNotExistException;
import com.example.assess2.mappers.TweetMapper;
import com.example.assess2.mappers.UserMappers;
import com.example.assess2.objects.Context;
import com.example.assess2.objects.Credentials;
import com.example.assess2.objects.Hashtag;
import com.example.assess2.objects.Tweet;
import com.example.assess2.objects.User;
import com.example.assess2.objectsdto.TweetDto;
import com.example.assess2.objectsdto.UserDto;
import com.example.assess2.repositories.HashtagRepository;
import com.example.assess2.repositories.TweetRepository;

@Service
public class TweetService {

	private TweetRepository tweetRepo;
	private UserService userService;
	private ValidationService validationService;
	private TweetMapper mapper;
	private HashtagRepository hashtagRepo;
	private UserMappers userMapper;

	public TweetService(TweetRepository tweetRepo, UserService userService, HashtagRepository hashtagRepo,
			ValidationService validationService, TweetMapper mapper, UserMappers userMapper) {
		this.tweetRepo = tweetRepo;
		this.userService = userService;
		this.validationService = validationService;
		this.hashtagRepo = hashtagRepo;
		this.mapper = mapper;
		this.userMapper = userMapper;
	}

	public List<TweetDto> getAll() {
		return tweetRepo.findByDeletedIsFalse().stream().sorted((a, b) -> {
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

	public List<Hashtag> getTweetTags(Integer id) throws TweetDoesNotExistException {
		if (validationService.tweetExistsById(id)) {
			return tweetRepo.findByIdAndDeletedIsFalse(id).getTags();
		} else {
			throw new TweetDoesNotExistException();
		}
	}

	public Context getTweetContext(Integer id) throws TweetDoesNotExistException {
		if (validationService.tweetExistsById(id)) {
			Tweet target = tweetRepo.findByIdAndDeletedIsFalse(id);
			Tweet current = target;
			List<Tweet> before = new ArrayList<Tweet>();
			List<Tweet> after = new ArrayList<Tweet>();

			// before
			while (current.getInReplyTo() != null) {
				current = current.getInReplyTo();
				before.add(current);
			}

			// after
			after = getRepliesR(target);
			after.remove(target);

			List<TweetDto> sortedBeforeDto = before.stream().sorted((a, b) -> {
				if (a.getPosted().after(b.getPosted()))
					return 0;
				else
					return 1;
			}).map(mapper::toDto).collect(Collectors.toList());

			List<TweetDto> sortedAfterDto = after.stream().sorted((a, b) -> {
				if (a.getPosted().after(b.getPosted()))
					return 0;
				else
					return 1;
			}).map(mapper::toDto).collect(Collectors.toList());

			return new Context(mapper.toDto(target), sortedBeforeDto, sortedAfterDto);
		} else {
			throw new TweetDoesNotExistException();
		}
	}

	public List<Tweet> getRepliesR(Tweet tweet) {
		List<Tweet> l = new ArrayList<Tweet>();
		if (tweet.getReplies() == null) {
			l.add(tweet);
			return l;
		}
		for (Tweet t : tweet.getReplies()) {
			l.addAll(getRepliesR(t));
		}
		return l;
	}

	public List<TweetDto> getReposts(Integer id) throws TweetDoesNotExistException {
		if (validationService.tweetExistsById(id)) {
			return tweetRepo.findByIdAndDeletedIsFalse(id).getReposts().stream().map(mapper::toDto)
					.collect(Collectors.toList());
		} else {
			throw new TweetDoesNotExistException();
		}
	}

	public List<UserDto> getMentions(Integer id) throws TweetDoesNotExistException {
		if (validationService.tweetExistsById(id)) {
			return tweetRepo.findByIdAndDeletedIsFalse(id).getMentions().stream().map(userMapper::toDto)
					.collect(Collectors.toList());
		} else {
			throw new TweetDoesNotExistException();
		}
	}

	@Transactional
	public TweetDto postTweet(String content, Credentials credentials)
			throws UserDoesNotExistException, CredentialsDoNotMatchException {
		if (validationService.userExistsAndActive(credentials.getUsername())) {
			if (!validationService.checkCredentials(
					userService.getUserByUsername(credentials.getUsername()).getCredentials().getUsername(),
					credentials)) {
				throw new CredentialsDoNotMatchException();
			}
			Tweet tweet = new Tweet(null, userService.getUserByUsername(credentials.getUsername()),
					System.currentTimeMillis(), content, null, null);
			List<Hashtag> tags = new ArrayList<Hashtag>();
			List<User> mentions = new ArrayList<User>();

			postTweetGetMentionsAndTags(tags, mentions, content);
			tweet.setMentions(mentions);
			tweet.setTags(tags);
			Tweet savedTweet = tweetRepo.save(tweet);

			for (User u : tweet.getMentions()) {
				u.getMentions().add(tweet);
			}

			userService.getUserByUsername(credentials.getUsername()).getTweets().add(savedTweet);

			return mapper.toDto(savedTweet);
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
					System.out.println(extractedString);
					if (validationService.userExists(extractedString)) {
						try {
							System.out.println(extractedString);
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
			if (c == '\64') {
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
					if (validationService.userExists(extractedString)) {
						try {
							System.out.println(extractedString);
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
					if (validationService.userExists(extractedString)) {
						try {
							System.out.println(extractedString);
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
				System.out.println(c);
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
			if (validationService.userExists(extractedString)) {
				try {
					System.out.println(extractedString);
					mentions.add(userService.getUserByUsername(extractedString));
				} catch (UserDoesNotExistException e) {
					e.printStackTrace();
				}
			}
			extractedString = "";
		}
	}

	public TweetDto deleteTweet(Integer id) throws TweetDoesNotExistException {
		if (tweetRepo.existsByIdAndDeletedIsFalse(id)) {
			Tweet tweet = tweetRepo.findByIdAndDeletedIsFalse(id);
			tweet.setDeleted(true);
			return mapper.toDto(tweetRepo.save(tweet));
		} else {
			throw new TweetDoesNotExistException();
		}
	}

	@Transactional
	public void likeTweet(Integer id, Credentials credentials)
			throws TweetDoesNotExistException, CredentialsDoNotMatchException, UserDoesNotExistException {
		if (validationService.userExistsAndActive(credentials.getUsername())) {
			if (!validationService.checkCredentials(
					userService.getUserByUsername(credentials.getUsername()).getCredentials().getUsername(),
					credentials)) {
				throw new CredentialsDoNotMatchException();
			}
			if (!validationService.tweetExistsById(id)) {
				throw new TweetDoesNotExistException();
			}

			User user = userService.getUserByUsername(credentials.getUsername());
			Tweet tweet = tweetRepo.findByIdAndDeletedIsFalse(id);

			user.getLikes().add(tweet);
			tweet.getLikedBy().add(user);
		} else {
			throw new UserDoesNotExistException();
		}
	}

	@Transactional
	public TweetDto replyTo(Integer id, Credentials credentials, String content)
			throws TweetDoesNotExistException, CredentialsDoNotMatchException, UserDoesNotExistException {
		if (validationService.userExistsAndActive(credentials.getUsername())) {
			if (!validationService.checkCredentials(
					userService.getUserByUsername(credentials.getUsername()).getCredentials().getUsername(),
					credentials)) {
				throw new CredentialsDoNotMatchException();
			}
			if (!validationService.tweetExistsById(id)) {
				throw new TweetDoesNotExistException();
			}

			Tweet tweet = new Tweet(null, userService.getUserByUsername(credentials.getUsername()),
					System.currentTimeMillis(), content, null, null);
			List<Hashtag> tags = new ArrayList<Hashtag>();
			List<User> mentions = new ArrayList<User>();

			postTweetGetMentionsAndTags(tags, mentions, content);
			tweet.setMentions(mentions);
			tweet.setTags(tags);
			Tweet savedTweet = tweetRepo.save(tweet);

			for (User u : tweet.getMentions()) {
				u.getMentions().add(tweet);
			}
			User user = userService.getUserByUsername(credentials.getUsername());
			user.getTweets().add(savedTweet);
			Tweet replyingTo = tweetRepo.findByIdAndDeletedIsFalse(id);
			replyingTo.getReplies().add(savedTweet);
			tweet.setInReplyTo(replyingTo);

			return mapper.toDto(savedTweet);
		} else {
			throw new UserDoesNotExistException();
		}
	}

	@Transactional
	public TweetDto repost(Integer id, Credentials credentials)
			throws TweetDoesNotExistException, CredentialsDoNotMatchException, UserDoesNotExistException {
		if(credentials == null || credentials.getUsername() == null || credentials.getPassword() == null) {
			throw new CredentialsDoNotMatchException();
		}
		if (validationService.userExistsAndActive(credentials.getUsername())) {
			if (!validationService.checkCredentials(
					userService.getUserByUsername(credentials.getUsername()).getCredentials().getUsername(),
					credentials)) {
				throw new CredentialsDoNotMatchException();
			}
			if (!validationService.tweetExistsById(id)) {
				throw new TweetDoesNotExistException();
			}

			Tweet tweetToRepost = tweetRepo.findByIdAndDeletedIsFalse(id);
			Tweet newTweet = new Tweet(tweetToRepost);
			User userToPostTo = userService.getUserByUsername(credentials.getUsername());

			Tweet savedTweet = tweetRepo.save(newTweet);
			userToPostTo.getTweets().add(savedTweet);
			tweetToRepost.getReposts().add(savedTweet);

			return mapper.toDto(savedTweet);

		} else {
			throw new UserDoesNotExistException();
		}
	}

}
