package com.example.assess2.objects;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.example.assess2.objectsdto.TweetDto;

@Entity
public class Tweet {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@ManyToOne
	private User author;
	
	private Timestamp posted;
	private String content;
	private Boolean deleted;
	
	@ManyToMany
	private List<User> mentions;
	
	@ManyToMany
	private List<Hashtag> tags;
	
	@ManyToOne
	private Tweet inReplyTo;
	
	@OneToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({CascadeType.SAVE_UPDATE})
	private List<Tweet> replies;
	
	@ManyToOne
	@Cascade({CascadeType.SAVE_UPDATE})
	private TweetDto inRepostOf;

	public Tweet() {
		
	}
	
	public Tweet(Integer id, User author, long time, String content, Tweet inReplyTo, TweetDto inRepostOf) {
		super();
		this.id = id;
		this.author = author;
		this.posted = new Timestamp(time);
		this.content = content;
		this.inReplyTo = inReplyTo;
		this.inRepostOf = inRepostOf;
		this.deleted = false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tweet other = (Tweet) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	

	public List<Tweet> getReplies() {
		return replies;
	}

	public void setReplies(List<Tweet> replies) {
		this.replies = replies;
	}

	public List<Hashtag> getTags() {
		return tags;
	}

	public void setTags(List<Hashtag> tags) {
		this.tags = tags;
	}

	public List<User> getMentions() {
		return mentions;
	}

	public void setMentions(List<User> mentions) {
		this.mentions = mentions;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Timestamp getPosted() {
		return posted;
	}

	public void setPosted(Timestamp posted) {
		this.posted = posted;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Tweet getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(Tweet inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public TweetDto getInRepostOf() {
		return inRepostOf;
	}

	public void setInRepostOf(TweetDto inRepostOf) {
		this.inRepostOf = inRepostOf;
	}

}
