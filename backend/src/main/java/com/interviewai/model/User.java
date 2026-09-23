package com.interviewai.model;

import com.interviewai.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Document(collection = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseAudit {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;

    @Builder.Default
    private Set<String> roles = Set.of(Constants.ROLE_USER);

    @Builder.Default
    private String plan = "FREE";

    private String avatarUrl;

    @Builder.Default
    private String status = "ACTIVE";

    private Instant lastLoginAt;
}