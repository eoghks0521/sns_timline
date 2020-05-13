package com.hackday.timeline.post.controller;// package com.hackday.timeline.post.controller;
//
// import java.util.List;
//
// import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
// import org.springframework.web.bind.annotation.ResponseBody;
// import org.springframework.web.bind.annotation.RestController;
//
// import com.hackday.timeline.post.domain.Post;
// import com.hackday.timeline.post.service.PostService;
// import com.hackday.timeline.user.domain.User;
// import com.hackday.timeline.user.service.UserService;
// import lombok.Builder;
// import lombok.Getter;
//
// @RestController
// public class PostAjaxController {
//
// 	private PostService postService;
//
// 	private UserService userService;
//
// 	@Autowired
// 	public PostAjaxController(PostService postService, UserService userService){
// 		this.postService = postService;
// 		this.userService = userService;
// 	}
//
// 	// @ResponseBody
// 	// @RequestMapping(value="/getPosts", method = RequestMethod.POST)
// 	// public PostsResponse getMyPosts(@RequestBody GetPostsRequest getPostsRequest) {
// 	// 	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
// 	// 	User user = userService.findByEmail(auth.getName());
// 	//
// 	// 	List<Post> posts = postService.getPosts(getPostsRequest.getLastIdOfPosts(), user.getId());
// 	// 	Long lastIdOfPosts = posts.isEmpty() ?
// 	// 		null : posts.get(posts.size() - 1).getId();
// 	//
// 	// 	PostsResponse result = PostsResponse.builder()
// 	// 		.posts(posts)
// 	// 		.lastIdOfPosts(lastIdOfPosts)
// 	// 		.build();
// 	// 	return result;
// 	// }
//
// 	// @ResponseBody
// 	// @RequestMapping(value="/friendPosts", method = RequestMethod.POST)
// 	// public PostsResponse getFriendPosts(@RequestBody GetPostsRequest getPostsRequest, Long userId) {
// 	// 	List<Post> posts = postService.getPosts(getPostsRequest.getLastIdOfPosts(), userId);
// 	// 	Long lastIdOfPosts = posts.isEmpty() ?
// 	// 		null : posts.get(posts.size() - 1).getId();
// 	//
// 	// 	PostsResponse result = PostsResponse.builder()
// 	// 		.posts(posts)
// 	// 		.lastIdOfPosts(lastIdOfPosts)
// 	// 		.build();
// 	// 	return result;
// 	// }
//
// 	@Getter
// 	static class GetPostsRequest{
// 		private Long lastIdOfPosts;
// 	}
//
// 	@Getter
// 	@Builder
// 	static class PostsResponse{
// 		private List<Post> posts;
// 		private Long lastIdOfPosts;
// 	}
//
//
// }