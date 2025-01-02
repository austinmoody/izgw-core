package gov.cdc.izgateway.security.principal;

import gov.cdc.izgateway.principal.provider.JwtPrincipalProvider;
import gov.cdc.izgateway.security.IzgPrincipal;
import gov.cdc.izgateway.security.JWTPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.*;

@Slf4j
@Component
public class JwtSharedSecretPrincipalProvider implements JwtPrincipalProvider {
    @Value("${jwt.shared-secret:}")
    private String sharedSecret;

    @Value("${jwt.roles-claim}")
    private String rolesClaim;

    @Value("${jwt.scopes-claim}")
    private String scopesClaim;

    private final GroupToRoleMapper groupToRoleMapper;
    private final ScopeToRoleMapper scopeToRoleMapper;
    private final JwtTokenExtractor jwtTokenExtractor;

    @Autowired
    public JwtSharedSecretPrincipalProvider(@Nullable GroupToRoleMapper groupToRoleMapper,
                                            @Nullable ScopeToRoleMapper scopeToRoleMapper,
                                            JwtTokenExtractor jwtTokenExtractor) {
        this.groupToRoleMapper = groupToRoleMapper;
        this.scopeToRoleMapper = scopeToRoleMapper;
        this.jwtTokenExtractor = jwtTokenExtractor;
    }

    @Override
    public IzgPrincipal createPrincipalFromJwt(HttpServletRequest request) {
        if (StringUtils.isBlank(sharedSecret)) {
            log.warn("No JWT shared secret was set. JWT authentication is disabled.");
            return null;
        }

        SecretKeySpec secretKey = new SecretKeySpec(sharedSecret.getBytes(), "HmacSHA256");

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
                    .withSecretKey(secretKey)
                    .build();
            jwtDecoder.setJwtValidator(validator);

            Jwt jwt;

            jwt = jwtDecoder.decode(token);

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
