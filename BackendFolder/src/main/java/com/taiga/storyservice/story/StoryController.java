package com.taiga.storyservice.story;

import com.taiga.storyservice.story.dto.AcceptanceCriteriaUpdateRequest;
import com.taiga.storyservice.story.dto.StoryRequest;
import com.taiga.storyservice.story.dto.StoryResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories")
@CrossOrigin(origins = "*") // allow JavaFX client
public class StoryController {

    private final StoryService service;

    public StoryController(StoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<StoryResponse> createStory(@Valid @RequestBody StoryRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}/acceptance-criteria")
    public ResponseEntity<StoryResponse> updateAC(@PathVariable Long id,
                                                  @Valid @RequestBody AcceptanceCriteriaUpdateRequest request) {
        return ResponseEntity.ok(service.updateAcceptanceCriteria(id, request.getAcceptanceCriteria()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryResponse> getStory(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }
}
