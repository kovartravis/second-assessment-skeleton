package com.example.assess2.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.assess2.exceptions.AlreadyFollowingUserException;
import com.example.assess2.exceptions.CredentialsDoNotMatchException;
import com.example.assess2.exceptions.NotFollowingUserException;
import com.example.assess2.exceptions.SomethingIsNullAndItShouldntBeException;
import com.example.assess2.exceptions.TweetDoesNotExistException;
import com.example.assess2.exceptions.UserAlreadyExistsException;
import com.example.assess2.exceptions.UserDoesNotExistException;
import com.example.assess2.mappers.UserMappers;
import com.example.assess2.objects.Credentials;
import com.example.assess2.objects.Profile;
import com.example.assess2.objects.Tweet;
import com.example.assess2.objects.User;
import com.example.assess2.objectsdto.UserDto;
import com.example.assess2.repositories.TweetRepository;
import com.example.assess2.repositories.UserRepository;

@Service
public class UserService {

	private UserRepository userRepo;
	private ValidationService validationService;
	private UserMappers mapper;
	private TweetRepository tweetRepo;

	public UserService(UserRepository userRepo, ValidationService validationService, UserMappers mapper,
			TweetRepository tweetRepo) {
		this.userRepo = userRepo;
		this.validationService = validationService;
		this.mapper = mapper;
		this.tweetRepo = tweetRepo;
	}

	public List<UserDto> getAll() {
		return userRepo.findByActiveIsTrue().stream().map(mapper::toDto).collect(Collectors.toList());
	}

	public User getUserByUsername(String username) throws UserDoesNotExistException {
		if (!validationService.userExistsAndActive(username)) {
			throw new UserDoesNotExistException();
		} else {
			return userRepo.findByCredentialsUsername(username);
		}
	}

	public UserDto getUserDtoByUsername(String username) throws UserDoesNotExistException {
		if (!validationService.userExistsAndActive(username)) {
			throw new UserDoesNotExistException();
		} else {
			return mapper.toDto(userRepo.findByCredentialsUsername(username));
		}
	}
		

	public List<UserDto> getFollowers(String username) throws UserDoesNotExistException {
		if (validationService.userExistsAndActive(username)) {
			return userRepo.findByFollowingCredentialsUsername(username).stream().map(mapper::toDto)
					.collect(Collectors.toList());
		} else {
			throw new UserDoesNotExistException();
		}
	}

	public List<UserDto> getFollowing(String username) throws UserDoesNotExistException {
		if (validationService.userExistsAndActive(username)) {
			return userRepo.findByCredentialsUsername(username).getFollowing().stream().map(mapper::toDto)
					.collect(Collectors.toList());
		} else {
			throw new UserDoesNotExistException();
		}
	}

	public List<UserDto> getLikes(Integer id) throws TweetDoesNotExistException {
		if (validationService.tweetExistsById(id)) {
			
			return userRepo.findDistinctByLikesAndActiveIsTrue(tweetRepo.findByIdAndDeletedIsFalse(id)).stream().map(mapper::toDto).collect(Collectors.toList());
		} else {
			throw new TweetDoesNotExistException();
		}
	}

	public UserDto postUser(Credentials credentials, Profile profile)
			throws UserAlreadyExistsException, SomethingIsNullAndItShouldntBeException {
		if (credentials == null || profile == null) {
			throw new SomethingIsNullAndItShouldntBeException();
		}
		if (profile.getEmail() == null || credentials.getPassword() == null || credentials.getUsername() == null) {
			throw new SomethingIsNullAndItShouldntBeException();
		}
		if (validationService.userExists(credentials.getUsername())) {
			if(validationService.userExistsAndActive(credentials.getUsername())) {
				throw new UserAlreadyExistsException();
			}
			if (validationService.checkCredentials(credentials.getUsername(), credentials)) {
				User user = userRepo.findByCredentialsUsername(credentials.getUsername());
				user.setActive(true);
				if (profile.getFirstName() != null) {
					user.getProfile().setFirstName(profile.getFirstName());
				}
				if (profile.getLastName() != null) {
					user.getProfile().setLastName(profile.getLastName());
				}
				if (profile.getPhone() != null) {
					user.getProfile().setPhone(profile.getPhone());
				}
				user.getProfile().setEmail(profile.getEmail());
				userRepo.save(user);
				try {
					undeleteTweets(user.getCredentials().getUsername());
				} catch (UserDoesNotExistException e) {
					e.printStackTrace();
				}
				return mapper.toDto(userRepo.save(user));
			}else {
				throw new UserAlreadyExistsException();
			}
		} else {
			User user = new User();
			user.setCredentials(credentials);
			user.setActive(true);
			user.setProfile(profile);
			user.setJoined(new Timestamp(System.currentTimeMillis()));
			return mapper.toDto(userRepo.save(user));
		}
	}

	public UserDto updateUser(String username, Credentials credentials, Profile profile)
			throws UserDoesNotExistException, CredentialsDoNotMatchException, SomethingIsNullAndItShouldntBeException {
		if (credentials == null || profile == null) {
			throw new SomethingIsNullAndItShouldntBeException();
		}
		if (credentials.getPassword() == null || credentials.getUsername() == null) {
			throw new SomethingIsNullAndItShouldntBeException();
		}
		if (validationService.userExistsAndActive(username)) {
			User user = userRepo.findByCredentialsUsername(username);
			if (validationService.checkCredentials(username, credentials)) {
				user.setCredentials(credentials);
				if (profile.getFirstName() != null) {
					user.getProfile().setFirstName(profile.getFirstName());
				}
				if (profile.getLastName() != null) {
					user.getProfile().setLastName(profile.getLastName());
				}
				if (profile.getPhone() != null) {
					user.getProfile().setPhone(profile.getPhone());
				}
				if (profile.getEmail() != null) {
					user.getProfile().setEmail(profile.getEmail());
				}

				return mapper.toDto(userRepo.save(user));
			} else {
				throw new CredentialsDoNotMatchException();
			}
		} else {
			throw new UserDoesNotExistException();
		}
	}

	public UserDto deleteUser(String username, Credentials credentials)
			throws UserDoesNotExistException, CredentialsDoNotMatchException {
		if (validationService.userExistsAndActive(username)) {
			User user = userRepo.findByCredentialsUsername(username);
			if (validationService.checkCredentials(username, credentials)) {
				user.setActive(false);
				deleteTweetsByUser(username);
				return mapper.toDto(userRepo.save(user));
			} else {
				throw new CredentialsDoNotMatchException();
			}
		} else {
			throw new UserDoesNotExistException();
		}
	}

	public void deleteTweetsByUser(String username) throws UserDoesNotExistException {
		List<Tweet> tweets = tweetRepo.findByAuthorAndDeletedIsFalse(getUserByUsername(username));
		for (Tweet t : tweets) {
			t.setDeleted(true);
			tweetRepo.save(t);
		}
	}

	public void undeleteTweets(String username) throws UserDoesNotExistException {
		List<Tweet> tweets = tweetRepo.findByAuthor(getUserByUsername(username));
		for (Tweet t : tweets) {
			t.setDeleted(false);
			tweetRepo.save(t);
		}
	}

	@Transactional
	public void followUser(String username, Credentials credentials) throws UserDoesNotExistException,
			CredentialsDoNotMatchException, AlreadyFollowingUserException, SomethingIsNullAndItShouldntBeException {
		if (credentials == null) {
			throw new SomethingIsNullAndItShouldntBeException();
		}
		if (credentials.getPassword() == null || credentials.getUsername() == null) {
			throw new SomethingIsNullAndItShouldntBeException();
		}
		if (validationService.userExistsAndActive(username)
				&& validationService.userExistsAndActive(credentials.getUsername())) {
			User user = userRepo.findByCredentialsUsername(credentials.getUsername());
			if (validationService.checkCredentials(user.getCredentials().getUsername(), credentials)) {
				User userToFollow = userRepo.findByCredentialsUsername(username);
				if (user.getFollowing().contains(userToFollow)) {
					throw new AlreadyFollowingUserException();
				} else {
					user.getFollowing().add(userToFollow);
					userRepo.save(user);
				}
			} else {
				throw new CredentialsDoNotMatchException();
			}
		} else {
			throw new UserDoesNotExistException();
		}
	}

	@Transactional
	public void unfollowUser(String username, Credentials credentials) throws UserDoesNotExistException,
			CredentialsDoNotMatchException, NotFollowingUserException, SomethingIsNullAndItShouldntBeException {
		if (credentials == null) {
			throw new SomethingIsNullAndItShouldntBeException();
		}
		if (credentials.getPassword() == null || credentials.getUsername() == null) {
			throw new SomethingIsNullAndItShouldntBeException();
		}
		if (validationService.userExistsAndActive(username)
				&& validationService.userExistsAndActive(credentials.getUsername())) {
			User user = userRepo.findByCredentialsUsername(credentials.getUsername());
			if (validationService.checkCredentials(user.getCredentials().getUsername(), credentials)) {
				User userToUnfollow = userRepo.findByCredentialsUsername(username);
				if (user.getFollowing().contains(userToUnfollow)) {
					user.getFollowing().remove(userToUnfollow);
					userRepo.save(user);
					userRepo.save(userToUnfollow);
				} else {
					throw new NotFollowingUserException();
				}
			} else {
				throw new CredentialsDoNotMatchException();
			}
		} else {
			throw new UserDoesNotExistException();
		}
	}

	public void save(User user) {
		userRepo.save(user);
	}

}
