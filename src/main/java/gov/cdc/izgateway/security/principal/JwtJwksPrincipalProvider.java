package gov.cdc.izgateway.security.principal;

import gov.cdc.izgateway.principal.provider.JwtPrincipalProvider;
import gov.cdc.izgateway.security.IzgPrincipal;
import gov.cdc.izgateway.security.JWTPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class JwtJwksPrincipalProvider implements JwtPrincipalProvider {
    @Value("${jwt.jwk-set-uri:}")
    private String jwkSetUri;

    @Value("${jwt.user-permissions-claim}")
    private String userPermissionsClaim;

    @Value("${jwt.scopes-claim}")
    private String scopesClaim;

    private final GroupToRoleMapper groupToRoleMapper;
    private final ScopeToRoleMapper scopeToRoleMapper;

    public JwtJwksPrincipalProvider(@Nullable GroupToRoleMapper groupToRoleMapper,
                                    @Nullable ScopeToRoleMapper scopeToRoleMapper) {
        this.groupToRoleMapper = groupToRoleMapper;
        this.scopeToRoleMapper = scopeToRoleMapper;
    }


    @Override
    public IzgPrincipal createPrincipalFromJwt(HttpServletRequest request) {
        if (StringUtils.isBlank(jwkSetUri)) {
            log.warn("No JWT set URI configured.  JWT authentication is disabled.");
            return null;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No JWT token found in Authorization header");
            return null;
        }

        JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

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
        principal.setOrganization(jwt.getClaim("organization"));
        principal.setValidFrom(Date.from(Objects.requireNonNull(jwt.getIssuedAt())));
        principal.setValidTo(Date.from(Objects.requireNonNull(jwt.getExpiresAt())));
        principal.setSerialNumber(jwt.getId());
        principal.setIssuer(jwt.getIssuer().toString());
        principal.setAudience(jwt.getAudience());
        addScopes(jwt.getClaim(scopesClaim), principal);
        addRolesFromGroups(jwt.getClaim(userPermissionsClaim), principal);
        addRolesFromScopes(jwt.getClaim(scopesClaim), principal);

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
        // should be "expressed as a list of space-delimited, case-sensitive strings"
        // However, Okta seems to send back an array of strings.
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
}
