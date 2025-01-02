package gov.cdc.izgateway.security.principal;

import gov.cdc.izgateway.principal.provider.JwtPrincipalProvider;
import gov.cdc.izgateway.security.IzgPrincipal;
import gov.cdc.izgateway.security.JWTPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class JwtJwksPrincipalProvider implements JwtPrincipalProvider {
    @Value("${jwt.jwk-set-uri:}")
    private String jwkSetUri;

    @Value("${jwt.roles-claim}")
    private String rolesClaim;

    @Value("${jwt.scopes-claim}")
    private String scopesClaim;

    private final GroupToRoleMapper groupToRoleMapper;
    private final ScopeToRoleMapper scopeToRoleMapper;
    private final JwtTokenExtractor jwtTokenExtractor;

    public JwtJwksPrincipalProvider(@Nullable GroupToRoleMapper groupToRoleMapper,
                                    @Nullable ScopeToRoleMapper scopeToRoleMapper,
                                    JwtTokenExtractor jwtTokenExtractor) {
        this.groupToRoleMapper = groupToRoleMapper;
        this.scopeToRoleMapper = scopeToRoleMapper;
        this.jwtTokenExtractor = jwtTokenExtractor;
    }


    @Override
    public IzgPrincipal createPrincipalFromJwt(HttpServletRequest request) {
        if (StringUtils.isBlank(jwkSetUri)) {
            log.warn("No JWT set URI configured.  JWT authentication is disabled.");
            return null;
        }

        try {
            String token = jwtTokenExtractor.extractToken(request);

            // We do not want to accept JWT tokens without From/To dates
            JwtTimestampValidator timestampValidator = new JwtTimestampValidator();
            DelegatingOAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                    timestampValidator,
                    new JwtClaimValidator<>(JwtClaimNames.EXP, Objects::nonNull),
                    new JwtClaimValidator<>(JwtClaimNames.IAT, Objects::nonNull)
            );

            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                    .withJwkSetUri(jwkSetUri)
                    .build();
            jwtDecoder.setJwtValidator(validator);

            Jwt jwt = jwtDecoder.decode(token);

            log.debug("JWT claims for current request: {}", jwt.getClaims());

            return new JWTPrincipal(jwt,
                    groupToRoleMapper,
                    scopeToRoleMapper,
                    rolesClaim,
                    scopesClaim
            );

        } catch (InvalidJwtTokenException e) {
            return null;
        } catch (Exception e) {
            log.warn("Issue processing JWT token", e);
            return null;
        }

    }
}
