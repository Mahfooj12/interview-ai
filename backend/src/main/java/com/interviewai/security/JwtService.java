// package com.interviewai.security;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;

// import javax.crypto.SecretKey;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Set;
// import java.util.function.Function;

// @Service
// public class JwtService {

//     private final SecretKey signingKey;
//     private final long accessTokenExpirationMs;
//     private final long refreshTokenExpirationMs;
//     private final String issuer;

//     public JwtService(
//             @Value("${app.jwt.secret}") String secret,
//             @Value("${app.jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
//             @Value("${app.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs,
//             @Value("${app.jwt.issuer}") String issuer) {
//         // Secret must be base64 or at least 64 bytes for HS512; decode if base64, else raw
//         byte[] keyBytes;
//         try {
//             keyBytes = Decoders.BASE64.decode(secret);
//         } catch (Exception e) {
//             keyBytes = secret.getBytes();
//         }
//         this.signingKey = Keys.hmacShaKeyFor(keyBytes);
//         this.accessTokenExpirationMs = accessTokenExpirationMs;
//         this.refreshTokenExpirationMs = refreshTokenExpirationMs;
//         this.issuer = issuer;
//     }

//     public String generateAccessToken(String userId, String email, Set<String> roles) {
//         Map<String, Object> claims = new HashMap<>();
//         claims.put("uid", userId);
//         claims.put("roles", roles);
//         claims.put("type", "ACCESS");
//         return buildToken(claims, email, accessTokenExpirationMs);
//     }

//     public String generateRefreshToken(String userId, String email) {
//         Map<String, Object> claims = new HashMap<>();
//         claims.put("uid", userId);
//         claims.put("type", "REFRESH");
//         return buildToken(claims, email, refreshTokenExpirationMs);
//     }

//     private String buildToken(Map<String, Object> claims, String subject, long ttlMs) {
//         Date now = new Date();
//         Date expiry = new Date(now.getTime() + ttlMs);
//         return Jwts.builder()
//                 .setClaims(claims)
//                 .setSubject(subject)
//                 .setIssuer(issuer)
//                 .setIssuedAt(now)
//                 .setExpiration(expiry)
//                 .signWith(signingKey, SignatureAlgorithm.HS512)
//                 .compact();
//     }

//     public String extractEmail(String token) {
//         return extractClaim(token, Claims::getSubject);
//     }

//     public String extractUserId(String token) {
//         return extractClaim(token, c -> c.get("uid", String.class));
//     }

//     public String extractTokenType(String token) {
//         return extractClaim(token, c -> c.get("type", String.class));
//     }

//     public boolean isTokenValid(String token, String expectedEmail) {
//         try {
//             String email = extractEmail(token);
//             return email != null && email.equals(expectedEmail) && !isExpired(token);
//         } catch (Exception e) {
//             return false;
//         }
//     }

//     public boolean isExpired(String token) {
//         return extractClaim(token, Claims::getExpiration).before(new Date());
//     }

//     public <T> T extractClaim(String token, Function<Claims, T> resolver) {
//         Claims claims = Jwts.parserBuilder()
//                 .setSigningKey(signingKey)
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//         return resolver.apply(claims);
//     }

//     public long getAccessTokenExpirationMs() {
//         return accessTokenExpirationMs;
//     }

//     public long getRefreshTokenExpirationMs() {
//         return refreshTokenExpirationMs;
//     }
// }

package com.interviewai.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final String issuer;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
            @Value("${app.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs,
            @Value("${app.jwt.issuer}") String issuer) {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception e) {
            keyBytes = secret.getBytes();
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
        this.issuer = issuer;
    }

    public String generateAccessToken(String userId, String email, Set<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("roles", roles);
        claims.put("type", "ACCESS");
        return buildToken(claims, email, accessTokenExpirationMs);
    }

    public String generateRefreshToken(String userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("type", "REFRESH");
        return buildToken(claims, email, refreshTokenExpirationMs);
    }

    private String buildToken(Map<String, Object> claims, String subject, long ttlMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlMs);
        return Jwts.builder()
                .claims(claims)                    // was setClaims
                .subject(subject)                  // was setSubject
                .issuer(issuer)                    // was setIssuer
                .issuedAt(now)                     // was setIssuedAt
                .expiration(expiry)                // was setExpiration
                .signWith(signingKey, Jwts.SIG.HS512) // was SignatureAlgorithm.HS512
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUserId(String token) {
        return extractClaim(token, c -> c.get("uid", String.class));
    }

    public String extractTokenType(String token) {
        return extractClaim(token, c -> c.get("type", String.class));
    }

    public boolean isTokenValid(String token, String expectedEmail) {
        try {
            String email = extractEmail(token);
            return email != null && email.equals(expectedEmail) && !isExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()              // was parserBuilder()
                .verifyWith(signingKey)            // was setSigningKey
                .build()
                .parseSignedClaims(token)          // was parseClaimsJws
                .getPayload();                     // was getBody
        return resolver.apply(claims);
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }
}