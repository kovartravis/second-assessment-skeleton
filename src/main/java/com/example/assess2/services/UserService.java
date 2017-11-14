package com.example.assess2.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.assess2.exceptions.AlreadyFollowingUserException;
import com.example.assess2.exceptions.CredentialsDoNotMatchException;
import com.example.assess2.exceptions.NotFollowingUserException;
import com.example.assess2.exceptions.UserAlreadyExistsException;
import com.example.assess2.exceptions.UserDoesNotExistException;
import com.example.assess2.mappers.UserMappers;
import com.example.assess2.objects.Credentials;
import com.example.assess2.objects.Profile;
import com.example.assess2.objects.User;
import com.example.assess2.objectsdto.UserDto;
import com.example.assess2.repositories.UserRepository;

@Service
public class UserService {

	private UserRepository userRepo;
	private ValidationService validationService;
	private UserMappers mapper;

	public UserService(UserRepository userRepo, ValidationService validationService, UserMappers mapper) {
		this.userRepo = userRepo;
		this.validationService = validationService;
		this.mapper = mapper;

		Credentials creds = new Credentials("travis", "a");
		Profile pro = new Profile(null, null, "travis@yahoo.com", null);
		userRepo.save(new User(null, System.currentTimeMillis(), pro, creds));
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
		if(validationService.userExistsAndActive(username)) {
			return userRepo.findByCredentialsUsername(username).getFollowers().stream().map(mapper::toDto).collect(Collectors.toList());
		}else {
			throw new UserDoesNotExistException();
		}
	}
	
	public List<UserDto> getFollowing(String username) throws UserDoesNotExistException {
		if(validationService.userExistsAndActive(username)) {
			return userRepo.findByCredentialsUsername(username).getFollowers().stream().map(mapper::toDto).collect(Collectors.toList());
		}else {
			throw new UserDoesNotExistException();
		}
	}

	public UserDto postUser(Credentials credentials, Profile profile) throws UserAlreadyExistsException {
		if (validationService.userExists(credentials.getUsername())) {
			User user = userRepo.findByCredentialsUsername(credentials.getUsername());
			if (user.getCredentials().equals(credentials)) {
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
				return mapper.toDto(userRepo.save(user));
			} else {
				throw new UserAlreadyExistsException();
			}
		}

		return mapper.toDto(userRepo.save(new User(null, System.currentTimeMillis(), profile, credentials)));
	}

	public UserDto updateUser(String username, Credentials credentials, Profile profile)
			throws UserDoesNotExistException, CredentialsDoNotMatchException {
		if (validationService.userExistsAndActive(username)) {
			User user = userRepo.findByCredentialsUsername(username);
			if (user.getCredentials().equals(credentials)) {
				user.setCredentials(credentials);
				user.setProfile(profile);
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
			if (user.getCredentials().equals(credentials)) {
				user.setActive(false);
				return mapper.toDto(userRepo.save(user));
			} else {
				throw new CredentialsDoNotMatchException();
			}
		} else {
			throw new UserDoesNotExistException();
		}
	}

	public void followUser(String username, Credentials credentials)
			throws UserDoesNotExistException, CredentialsDoNotMatchException, AlreadyFollowingUserException {
		if (validationService.userExistsAndActive(username)
				&& validationService.userExistsAndActive(credentials.getUsername())) {
			User user = userRepo.findByCredentialsUsername(credentials.getUsername());
			if (user.getCredentials().equals(credentials)) {
				User userToFollow = userRepo.findByCredentialsUsername(username);
				if (user.getFollowing().contains(userToFollow)) {
					throw new AlreadyFollowingUserException();
				} else {
					user.getFollowing().add(userToFollow);
					userToFollow.getFollowers().add(user);
				}
			} else {
				throw new CredentialsDoNotMatchException();
			}
		} else {
			throw new UserDoesNotExistException();
		}
	}

	public void unfollowUser(String username, Credentials credentials)
			throws UserDoesNotExistException, CredentialsDoNotMatchException, NotFollowingUserException {
		if (validationService.userExistsAndActive(username)
				&& validationService.userExistsAndActive(credentials.getUsername())) {
			User user = userRepo.findByCredentialsUsername(credentials.getUsername());
			if (user.getCredentials().equals(credentials)) {
				User userToUnfollow = userRepo.findByCredentialsUsername(username);
				if (user.getFollowing().contains(userToUnfollow)) {
					user.getFollowing().remove(userToUnfollow);
					userToUnfollow.getFollowers().remove(user);
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

}
