package com.example.ideaboard.auth.controller;

import com.example.ideaboard.auth.dto.CommentDTO;
import com.example.ideaboard.auth.model.Comment;
import com.example.ideaboard.auth.model.UserStory;
import com.example.ideaboard.auth.repository.CommentRepository;
import com.example.ideaboard.auth.repository.UserStoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserStoryRepository userStoryRepository;

    // Get all comments for a specific user story
    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByStory(@PathVariable Long storyId) {
        List<Comment> comments = commentRepository.findByUserStoryIdOrderByCreatedAtDesc(storyId);
        List<CommentDTO> commentDTOs = comments.stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDTOs);
    }

    // Add a new comment to a user story
    @PostMapping("/story/{storyId}")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long storyId, @RequestBody Comment comment) {
        UserStory userStory = userStoryRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("User story not found with id: " + storyId));
        
        comment.setUserStory(userStory);
        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.ok(new CommentDTO(savedComment));
    }

    // Update a comment
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId, @RequestBody Comment commentDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        
        comment.setContent(commentDetails.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return ResponseEntity.ok(new CommentDTO(updatedComment));
    }

    // Delete a comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        
        commentRepository.delete(comment);
        return ResponseEntity.ok().body("Comment deleted successfully");
    }

    // Get all comments 
    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        List<CommentDTO> commentDTOs = comments.stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentDTOs);
    }
}
