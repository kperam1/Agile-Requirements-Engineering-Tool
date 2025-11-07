package com.taiga.storyservice.story.dto;

import jakarta.validation.constraints.NotBlank;

public class AcceptanceCriteriaUpdateRequest {

    @NotBlank
    private String acceptanceCriteria;

    public String getAcceptanceCriteria() { return acceptanceCriteria; }
    public void setAcceptanceCriteria(String acceptanceCriteria) { this.acceptanceCriteria = acceptanceCriteria; }
}
