package com.interviewai.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;  // ACTIVE | SUSPENDED

    private String plan;    // FREE | PRO | TEAM

    private Set<String> roles;
}
