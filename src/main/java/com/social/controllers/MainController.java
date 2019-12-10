package com.social.controllers;

import com.social.domain.Post;
import com.social.domain.User;
import com.social.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class MainController {

    @Autowired
    private PostRepository postRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "redirect:/main";
    }

    @GetMapping("/main")
    public String main (Map<String, Object> model){
        List<Post> posts = postRepository.findAll();
        model.put("posts", posts);
        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag,
            @RequestParam("file") MultipartFile file,
            Map<String, Object> model) throws IOException {
        Post post = new Post(text, tag, user);

        if (file != null) {
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFileName));

            post.setFilename(resultFileName);
        }

        postRepository.save(post);

        List<Post> posts = postRepository.findAll();
        model.put("posts", posts);
        return "main";
    }

    @PostMapping("filter")
    public String filter(@RequestParam String filter, Map<String, Object> model){
        Iterable<Post> posts;
        if (filter != null && !filter.isEmpty()) {
            posts = postRepository.findByTag(filter);
        } else {
            posts = postRepository.findAll();
        }
        model.put("posts", posts);
        return "main";
    }
}
