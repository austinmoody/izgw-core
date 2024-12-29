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

    @Value("${jwt.user-permissions-claim}")
    private String userPermissionsClaim;

    @Value("${jwt.scopes-claim}")
    private String scopesClaim;


    private final GroupToRoleMapper groupToRoleMapper;
    private final ScopeToRoleMapper scopeToRoleMapper;

    @Autowired
    public JwtSharedSecretPrincipalProvider(@Nullable GroupToRoleMapper groupToRoleMapper,
                                               @Nullable ScopeToRoleMapper scopeToRoleMapper) {
        this.groupToRoleMapper = groupToRoleMapper;
        this.scopeToRoleMapper = scopeToRoleMapper;
    }

    @Override
    public IzgPrincipal createPrincipalFromJwt(HttpServletRequest request) {
        if (StringUtils.isBlank(sharedSecret)) {
            log.warn("No JWT shared secret was set. JWT authentication is disabled.");
            return null;
        }

        SecretKeySpec secretKey = new SecretKeySpec(sharedSecret.getBytes(), "HmacSHA256");

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No JWT token found in Authorization header");
            return null;
        }

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

        try {
            String token = authHeader.substring(7);
            jwt = jwtDecoder.decode(token);
        } catch (Exception e) {
            log.warn("Error parsing JWT token", e);
            return null;
        }

        IzgPrincipal principal = new JWTPrincipal();
        principal.setName(jwt.getSubject());
        principal.setOrganization(getClaimNestedAsString(jwt, "organization"));
        principal.setValidFrom(Date.from(Objects.requireNonNull(jwt.getIssuedAt())));
        principal.setValidTo(Date.from(Objects.requireNonNull(jwt.getExpiresAt())));
        principal.setSerialNumber(jwt.getId());
        // Nimbus doesn't like non-URI issuers, pull via raw claim
        principal.setIssuer(getClaimNestedAsString(jwt, "iss"));
        principal.setAudience(jwt.getAudience());
        addScopes(getClaimNested(jwt, scopesClaim), principal);
        addRolesFromScopes(getClaimNested(jwt, scopesClaim), principal);
        addRolesFromGroups(getClaimNested(jwt, userPermissionsClaim), principal);
        log.debug("JWT claims for current request: {}", jwt.getClaims());
        return principal;
    }

    private void addScopes(Object scopesClaim, IzgPrincipal principal) {
        TreeSet<String> scopes = extractClaimList(scopesClaim);
        principal.setScopes(scopes);
    }

    private void addRolesFromScopes(Object scopesClaim, IzgPrincipal principal) {
        if (scopeToRoleMapper == null) {
            log.debug("No scope to role mapper was set. Skipping scope to role mapping.");
            return;
        }
        TreeSet<String> scopes = extractClaimList(scopesClaim);
        principal
                .getRoles()
                .addAll(
                        scopeToRoleMapper.mapScopesToRoles(scopes)
                );
    }

    private TreeSet<String> extractClaimList(Object scopeList) {
        // Oauth2 RFC (https://www.rfc-editor.org/rfc/rfc6749) states scopes
        // should be "expressed as a list of space-
        //   delimited, case-sensitive strings"
        // However Okta seems to send back array of strings.
        TreeSet<String> scopes = new TreeSet<>();
        if (scopeList instanceof String scopeString) {
            if (!StringUtils.isEmpty(scopeString)) {
                Collections.addAll(scopes, scopeString.split(" "));
            }
        } else if (scopeList instanceof Collection<?> collection) {
            collection.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .forEach(scopes::add);
        }
        return scopes;
    }

    private void addRolesFromGroups(Object groupsClaim, IzgPrincipal principal) {
        if (groupToRoleMapper == null) {
            log.debug("No group to role mapper was set. Skipping group to role mapping.");
            return;
        }

        TreeSet<String> groupsList = extractClaimList(groupsClaim);
        if (groupsList.isEmpty()) {
            return;
        }

        principal
                .getRoles()
                .addAll(
                        groupToRoleMapper.mapGroupsToRoles(groupsList)
                );
    }

    private String getClaimNestedAsString(Jwt jwt, String claimName) {
        Object claimValue = getClaimNested(jwt, claimName);
        return claimValue != null ? claimValue.toString() : null;
    }

    private Object getClaimNested(Jwt jwt, String claimPath) {
        String[] claimPathParts = claimPath.split("\\.");
        Object claim = jwt.getClaim(claimPathParts[0]);

        if (claimPathParts.length > 1 && !(claim instanceof Map<?,?>)) {
            /*
             Catches situation where you have configured groups.access.
             The initial pull of groups returns an array list.
             Without this, we'd end up returning that pull from groups
             as the claim Object, and ignore the fact that the user had
             configured another level.
             Seemed dangerous.
            */
            return null;
        }

        if (claim instanceof Map<?, ?> map && claimPathParts.length > 1) {
            for (int i = 1; i < claimPathParts.length; i++) {
                claim = map.get(claimPathParts[i]);
                if (claim instanceof Map<?, ?>) {
                    map = (Map<?, ?>) claim;
                } else if (i < claimPathParts.length - 1) {
                    /*
                    Again, catches a situation where the user has configured groups.level1.level2 and at level1
                    we have obtained a non-Map. So basically level2 didn't exist, but we don't want to return
                    the value pulled from level1.
                     */
                    return null;
                }
            }
        }
        return claim;
    }
}
