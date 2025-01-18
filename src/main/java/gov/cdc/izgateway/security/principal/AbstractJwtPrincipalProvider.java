package gov.cdc.izgateway.security.principal;

import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import gov.cdc.izgateway.principal.provider.JwtPrincipalProvider;
import gov.cdc.izgateway.security.IzgPrincipal;
import gov.cdc.izgateway.security.JWTPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;

import java.text.ParseException;
import java.time.Instant;
import java.util.Map;

@Slf4j
public abstract class AbstractJwtPrincipalProvider implements JwtPrincipalProvider {

    @Value("${jwt.roles-claim}")
    private String rolesClaim;

    @Value("${jwt.scopes-claim}")
    private String scopesClaim;

    protected final GroupToRoleMapper groupToRoleMapper;
    protected final ScopeToRoleMapper scopeToRoleMapper;
    protected final JwtTokenExtractor jwtTokenExtractor;

    protected AbstractJwtPrincipalProvider(
            @Nullable GroupToRoleMapper groupToRoleMapper,
            @Nullable ScopeToRoleMapper scopeToRoleMapper,
            JwtTokenExtractor jwtTokenExtractor) {
        this.groupToRoleMapper = groupToRoleMapper;
        this.scopeToRoleMapper = scopeToRoleMapper;
        this.jwtTokenExtractor = jwtTokenExtractor;
    }

    protected abstract NimbusJwtDecoder createDecoder();

    protected abstract boolean validConfiguration();

    protected DelegatingOAuth2TokenValidator<Jwt> createValidator() {
        JwtTimestampValidator timestampValidator = new JwtTimestampValidator();

        // Validator for 'exp' presence - required
        OAuth2TokenValidator<Jwt> expPresenceValidator = token -> {
            if (!token.getClaims().containsKey("exp")) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Token must contain exp claim", null)
                );
            }
            return OAuth2TokenValidatorResult.success();
        };

        // Custom validator for 'iat' OR 'nbf' presence in raw payload
        OAuth2TokenValidator<Jwt> timeConstraintValidator = token -> {
            try {
                SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token.getTokenValue());
                Map<String, Object> rawClaims = signedJWT.getJWTClaimsSet().getClaims();

                boolean hasIat = rawClaims.containsKey("iat");
                boolean hasNbf = rawClaims.containsKey("nbf");

                if (!hasIat && !hasNbf) {
                    return OAuth2TokenValidatorResult.failure(
                            new OAuth2Error("invalid_token",
                                    "Token must contain either iat or nbf claim", null)
                    );
                }
            } catch (ParseException e) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Failed to parse claims from JWT", null)
                );
            }
            return OAuth2TokenValidatorResult.success();
        };

        OAuth2TokenValidator<Jwt> futureIatCheck = token -> {
            try {
                SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token.getTokenValue());
                Map<String, Object> rawClaims = signedJWT.getJWTClaimsSet().getClaims();

                boolean hasIat = rawClaims.containsKey("iat");

                if (hasIat) {
                    // check to see if iat is in the future
                    Instant iat = token.getIssuedAt();
                    if ((iat != null) && iat.isAfter(Instant.now())) {
                        return OAuth2TokenValidatorResult.failure(
                                new OAuth2Error("invalid_token", "iat cannot be in the future", null)
                        );
                    }
                }
            } catch (ParseException e) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Failed to parse claims from JWT", null)
                );
            }
            return OAuth2TokenValidatorResult.success();
        };


        return new DelegatingOAuth2TokenValidator<>(
                timestampValidator,
                expPresenceValidator,
                timeConstraintValidator,
                futureIatCheck
        );
    }

    @Override
    public IzgPrincipal createPrincipalFromJwt(HttpServletRequest request) {
        if (!validConfiguration()) {
            return null;
        }

        try {
            String token = jwtTokenExtractor.extractToken(request);

            NimbusJwtDecoder jwtDecoder = createDecoder();
            jwtDecoder.setJwtValidator(createValidator());

            Jwt jwt = jwtDecoder.decode(token);

            log.debug("JWT claims for current request: {}", jwt.getClaims());

            return new JWTPrincipal(jwt,
                    rolesClaim,
                    scopesClaim,
                    groupToRoleMapper,
                    scopeToRoleMapper
            );

        } catch (InvalidJwtTokenException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Issue processing JWT token: {}", e.getMessage());
            return null;
        }
    }
}
