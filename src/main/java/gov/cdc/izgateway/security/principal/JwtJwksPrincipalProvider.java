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

        addRolesFromGroups(jwt.getClaimAsStringList(userPermissionsClaim), principal);

        addRolesFromScopes(jwt.getClaimAsString(scopesClaim), principal);

        return principal;
    }

    private void addRolesFromScopes(String scopesList, IzgPrincipal principal) {
        if (scopeToRoleMapper == null) {
            log.debug("No scope to role mapper was set. Skipping scope to role mapping.");
            return;
        }

        TreeSet<String> scopes = extractScopes(scopesList);
        principal.getRoles().addAll(scopeToRoleMapper.mapScopesToRoles(scopes));
    }

    private TreeSet<String> extractScopes(String scopesList) {
        TreeSet<String> scopes = new TreeSet<>();

        if (!StringUtils.isEmpty(scopesList)) {
            Collections.addAll(scopes, scopesList.split(" "));
        }
        return scopes;
    }

    private void addRolesFromGroups(List<String> groupsList, IzgPrincipal principal) {
        if (groupToRoleMapper == null) {
            log.debug("No group to role mapper was set. Skipping group to role mapping.");
            return;
        }

        if (groupsList == null || groupsList.isEmpty()) {
            return;
        }
        Set<String> groups = new TreeSet<>(groupsList);

        Set<String> roles = groupToRoleMapper.mapGroupsToRoles(groups);
        principal.getRoles().addAll(roles);
    }
}
