package com.hackday.timeline.post.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.hackday.timeline.image.service.ImageService;
import com.hackday.timeline.member.domain.Member;
import com.hackday.timeline.post.domain.Post;
import com.hackday.timeline.post.request.InsertPostDto;
import com.hackday.timeline.post.repository.PostRepository;
import com.hackday.timeline.image.domain.Image;
import com.hackday.timeline.utils.s3.S3Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;

	private final ImageService imageService;

	private final S3Service s3Service;

	public PostServiceImpl(PostRepository postRepository,
		ImageService imageService,
		S3Service s3Service) {
		this.postRepository = postRepository;
		this.imageService = imageService;
		this.s3Service = s3Service;
	}

	@Override
	@CacheEvict(value = "minIdOfPosts", key = "'myMinIdOfPosts-' +  #member.userNo")
	public Post insertPost(InsertPostDto insertPostDto, Member member) throws IOException {
		Post post = insertPostDto.toEntity(member, null);

		if(insertPostDto.getImage() != null) {
			String randomUUID = UUID.randomUUID().toString();
			String path = s3Service.postImageUpload(insertPostDto.getImage(), randomUUID);
			Image image = imageService.saveImage(path, randomUUID);
			post.setImage(image);
		}

		return postRepository.save(post);
	}

	@Override
	public boolean hasErrors(InsertPostDto insertPostDto, BindingResult bindingResult) {
		return bindingResult.hasErrors() ? true : false;
	}

	@Override
	public void modifyPost(InsertPostDto insertPostDto, Long postId, Member member) {
		Post post = postRepository.findById(postId).get();
		post.setContent(insertPostDto.getContent());
	}

	@Override
	@CacheEvict(value = "minIdOfPosts", key = "'myMinIdOfPosts-' +  #member.userNo")
	public void deletePost(Long postId, Long userId) {
		Post post = postRepository.findOneById(postId);

		if(post.getImage() != null) {
			imageService.deleteImage(post.getImage().getId());
		}

		postRepository.deleteByIdAndUser_UserNo(postId, userId);
	}

	@Override
	@Transactional(readOnly=true)
	@Cacheable(cacheNames="posts", key = "'myFeeds-' + #id + '-' + #userId")
	public List<Post> getPosts(Long id, Long userId) {
		return id == null ?
			this.postRepository.findByUserId(userId) :
			this.postRepository.findByIdAndUserId(userId, id);
	}

	@Override
	@Transactional(readOnly=true)
	@Cacheable(cacheNames="subsPosts", key = "'feeds-' + #id + '-' + #userId")
	public List<Post> getSubsPosts(Long id, Long userId) {
		return id == null ?
			this.postRepository.findByUserId(userId) :
			this.postRepository.findByIdAndUserId(userId, id);
	}

	@Override
	@Transactional(readOnly=true)
	@Cacheable(value = "minIdOfPosts", key = "'myMinIdOfPosts-' + #userId")
	public Long getMinIdOfPosts(Long userId) {
		return postRepository.findMinIdByUserId(userId);
	}

	@Override
	@Transactional(readOnly=true)
	@Cacheable(value = "minIdOfSubsPosts", key = "'subsMinIdOfPosts-' + #userId")
	public Long getMinIdOfSubsPosts(Long userId) {
		return postRepository.findMinIdBySubsUserId(userId);
	}

	@Override
	@Transactional(readOnly=true)
	@Cacheable(cacheNames="timeline", key = "'timeline-' + #postId + '-' + #userId")
	public List<Post> getFeeds(Long postId, Long userId) {
		return postId == null ?
			this.postRepository.findBySubscriptionsUserId(userId) :
			this.postRepository.findByIdAndSubsUserId(postId, userId);
	}

}
