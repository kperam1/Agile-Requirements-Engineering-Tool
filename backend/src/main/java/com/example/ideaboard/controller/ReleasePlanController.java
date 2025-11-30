package com.example.ideaboard.controller;

import com.example.ideaboard.model.ReleasePlan;
import com.example.ideaboard.repository.ReleasePlanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/releases")
public class ReleasePlanController {

    private final ReleasePlanRepository repo;

    public ReleasePlanController(ReleasePlanRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<ReleasePlan> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReleasePlan> get(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReleasePlan> create(@RequestBody ReleasePlan r) {
        ReleasePlan saved = repo.save(r);
        return ResponseEntity.created(URI.create("/api/releases/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReleasePlan> update(@PathVariable Long id, @RequestBody ReleasePlan r) {
        return repo.findById(id).map(existing -> {
            existing.setName(r.getName());
            existing.setVersion(r.getVersion());
            existing.setDescription(r.getDescription());
            existing.setStartDate(r.getStartDate());
            existing.setEndDate(r.getEndDate());
            existing.setProgress(r.getProgress());
            existing.setSprints(r.getSprints());
            existing.setTeam(r.getTeam());
            ReleasePlan saved = repo.save(existing);
            return ResponseEntity.ok(saved);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
