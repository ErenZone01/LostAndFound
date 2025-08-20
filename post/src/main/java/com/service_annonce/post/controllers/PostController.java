package com.service_annonce.post.controllers;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.service_annonce.post.models.Post;
import com.service_annonce.post.services.Create;
import com.service_annonce.post.services.Delete;
import com.service_annonce.post.services.PostService;
import com.service_annonce.post.services.Update;

import org.springframework.web.bind.annotation.GetMapping;

import com.service_annonce.post.models.PostDTO;


@RestController
@RequestMapping("/api")
public class PostController {

    private final Create createService;
    private final Update updateService;
    private final Delete deleteService;
    private final PostService postService;

    public PostController(Create createService, Update updateService, Delete deleteService, PostService postService) {
        this.createService = createService;
        this.updateService = updateService;
        this.deleteService = deleteService;
        this.postService = postService;
    }

    @GetMapping("/getAllPostById")    
    public List<PostDTO> getAllByUserId (@RequestHeader(value = "X-User-Id", required = true) String userId){
        System.out.println("userId:" + userId);
        return postService.getAllPostsByUserId(userId);
    }

    @GetMapping("/getPostById") 
    public PostDTO getByUserId (@RequestHeader(value = "X-User-Id", required = true) String userId){
        return postService.getPostById(userId);
    }



    @PostMapping("/create")
    public String createPost(@RequestHeader(value = "X-User-Id", required = true) String userId, @RequestParam("image") MultipartFile image, @RequestPart("post") @Validated Post post) {
        return createService.createPost(userId, post, image);
    }

    @PutMapping("/update/{id_Post}")
    public String updatePost(@PathVariable String id_Post, @RequestPart("image") MultipartFile image,
            @RequestPart("post") @Validated Post post) {
        return updateService.updatePost(id_Post, post, image);
    }

    @DeleteMapping("/delete/{id_Post}")
    public String deletePost(@PathVariable String id_Post) {
        System.out.println("Deleting post with ID: " + id_Post);
        return deleteService.deletePost(id_Post);
    }
}
