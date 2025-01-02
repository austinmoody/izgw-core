package gov.cdc.izgateway.security.principal;

import gov.cdc.izgateway.principal.provider.JwtPrincipalProvider;
import gov.cdc.izgateway.security.IzgPrincipal;
import gov.cdc.izgateway.security.JWTPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

import java.util.Objects;

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
        return new DelegatingOAuth2TokenValidator<>(
                timestampValidator,
                new JwtClaimValidator<>(JwtClaimNames.EXP, Objects::nonNull),
                new JwtClaimValidator<>(JwtClaimNames.IAT, Objects::nonNull)
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
