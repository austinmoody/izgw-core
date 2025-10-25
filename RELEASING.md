# Release Guide for izgw-core

This document describes the release process for the IZ Gateway Core library.

## Table of Contents
- [Overview](#overview)
- [Release Process](#release-process)
- [Hotfix Process](#hotfix-process)
- [Pre-Release Checklist](#pre-release-checklist)
- [Manual Release (Advanced)](#manual-release-advanced)
- [Troubleshooting](#troubleshooting)

## Overview

izgw-core follows [Semantic Versioning](https://semver.org/):
- **MAJOR** version: Incompatible API changes
- **MINOR** version: New functionality (backwards-compatible)
- **PATCH** version: Bug fixes (backwards-compatible)

**Version Format:**
- Development: `X.Y.Z-SNAPSHOT` (e.g., `2.3.0-SNAPSHOT`)
- Release: `X.Y.Z` (e.g., `2.3.0`)

## Release Process

### Automated Release (Recommended)

The automated release process is handled by GitHub Actions and performs all necessary steps.

#### Step 1: Prepare for Release

1. Ensure you're on the latest `main` or `develop` branch:
   ```bash
   git checkout main
   git pull origin main
   ```

2. Verify the current version in `pom.xml`:
   ```bash
   mvn help:evaluate -Dexpression=project.version -q -DforceStdout
   ```

3. Run the pre-release checklist (see below)

#### Step 2: Trigger the Release Workflow

1. Go to **Actions** → **Release** in GitHub
2. Click **Run workflow**
3. Fill in the parameters:
   - **Release version**: The version to release (e.g., `2.3.0`)
   - **Next SNAPSHOT version**: The next development version (e.g., `2.4.0-SNAPSHOT`)
   - **Skip tests**: Only check this for emergency releases (not recommended)

4. Click **Run workflow**

#### Step 3: Monitor the Release

The workflow will:
1. ✅ Validate version formats
2. ✅ Check for SNAPSHOT dependencies
3. ✅ Run full test suite
4. ✅ Run OWASP dependency check
5. ✅ Update version to release version
6. ✅ Build and package
7. ✅ Deploy to GitHub Packages
8. ✅ Create Git tag (`vX.Y.Z`)
9. ✅ Generate changelog from commits
10. ✅ Create GitHub Release
11. ✅ Bump to next SNAPSHOT version
12. ✅ Push changes back to repository

#### Step 4: Post-Release Tasks

1. Review the [GitHub Release](https://github.com/IZGateway/izgw-core/releases)
2. Verify the artifact is available in [GitHub Packages](https://github.com/IZGateway/izgw-core/packages)
3. Notify consuming applications:
   - Send notification to dependent teams
   - Update integration documentation
   - Create announcements in appropriate channels

4. Update the changelog (if needed):
   - Add release notes to `CHANGELOG.md`
   - Document breaking changes
   - Highlight new features

## Hotfix Process

Hotfixes are used for emergency patches to production releases.

### When to Use Hotfix

- Critical security vulnerability
- Production-breaking bug
- Data corruption issue

### Hotfix Steps

#### Step 1: Identify Base Version

Determine which release needs the hotfix (e.g., `2.3.0`)

#### Step 2: Trigger Hotfix Workflow

1. Go to **Actions** → **Hotfix Release** in GitHub
2. Click **Run workflow**
3. Fill in the parameters:
   - **Base version**: The version to hotfix (e.g., `2.3.0`)
   - **Hotfix version**: The new patch version (e.g., `2.3.1`)

4. Click **Run workflow**

#### Step 3: Apply Hotfix Changes

The workflow will create a hotfix branch and pause. You need to:

1. Checkout the hotfix branch:
   ```bash
   git fetch origin
   git checkout hotfix/v2.3.1
   ```

2. Apply your fix:
   ```bash
   # Make your changes
   git add .
   git commit -m "fix: critical issue description"
   git push origin hotfix/v2.3.1
   ```

#### Step 4: Complete the Hotfix

1. Return to **Actions** → **Hotfix Release**
2. **Re-run** the workflow with the same parameters
3. The workflow will:
   - Test your changes
   - Build and deploy
   - Create a new release
   - Merge back to `main`

#### Step 5: Cherry-pick to Development

If needed, cherry-pick the fix to your development branch:
```bash
git checkout develop
git cherry-pick <commit-hash>
git push origin develop
```

## Pre-Release Checklist

Before triggering a release, verify:

### Code Quality
- [ ] All CI/CD checks pass on `main`
- [ ] No failing tests
- [ ] Code review completed for all PRs
- [ ] No known critical bugs

### Dependencies
- [ ] All dependencies are on release versions (no SNAPSHOTs)
  ```bash
  # Check for SNAPSHOT dependencies (excluding parent BOM)
  mvn dependency:list | grep SNAPSHOT | grep -v izgw-bom
  ```
- [ ] Security vulnerabilities addressed
  ```bash
  mvn org.owasp:dependency-check-maven:check
  ```

### Documentation
- [ ] README.md is up to date
- [ ] API documentation is current
- [ ] Breaking changes are documented
- [ ] Migration guide prepared (if needed)

### Version Numbers
- [ ] Release version follows semantic versioning
- [ ] Version is not already tagged
  ```bash
  git tag -l "v2.3.0"  # Should return nothing
  ```

### Communication
- [ ] Dependent teams notified of upcoming release
- [ ] Release notes drafted
- [ ] Deployment schedule coordinated

## Manual Release (Advanced)

If you need to perform a manual release without the GitHub Actions workflow:

### Prerequisites
```bash
# Configure Maven settings with GitHub credentials
cat > ~/.m2/settings.xml << EOF
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
EOF
```

### Steps

1. **Update version to release**:
   ```bash
   mvn versions:set -DnewVersion=2.3.0 -DgenerateBackupPoms=false
   git add pom.xml
   git commit -m "chore: release version 2.3.0"
   ```

2. **Run tests and checks**:
   ```bash
   mvn clean test
   mvn org.owasp:dependency-check-maven:check
   ```

3. **Build and deploy**:
   ```bash
   mvn clean deploy -DskipTests
   ```

4. **Create and push tag**:
   ```bash
   git tag -a v2.3.0 -m "Release version 2.3.0"
   git push origin v2.3.0
   ```

5. **Bump to next SNAPSHOT**:
   ```bash
   mvn versions:set -DnewVersion=2.4.0-SNAPSHOT -DgenerateBackupPoms=false
   git add pom.xml
   git commit -m "chore: bump version to 2.4.0-SNAPSHOT"
   git push origin main
   ```

6. **Create GitHub Release manually** via the GitHub UI

## Troubleshooting

### Release Workflow Failed

**Issue**: SNAPSHOT dependencies detected
```
Solution:
1. Check which dependencies are SNAPSHOTs:
   mvn dependency:list | grep SNAPSHOT
2. Update dependencies to release versions in pom.xml
3. Re-run the release workflow
```

**Issue**: Tests failed
```
Solution:
1. Review the test logs in GitHub Actions
2. Fix failing tests on main branch
3. Push fixes and re-run release workflow
```

**Issue**: Deploy to GitHub Packages failed
```
Solution:
1. Verify GITHUB_TOKEN has write:packages permission
2. Check Maven settings.xml configuration
3. Ensure repository settings allow package publishing
```

### Version Conflicts

**Issue**: Tag already exists
```bash
# Delete local tag
git tag -d v2.3.0

# Delete remote tag (use with caution!)
git push origin :refs/tags/v2.3.0
```

**Issue**: Version already deployed to GitHub Packages
```
Solution:
You cannot overwrite a published version in GitHub Packages.
Bump to the next patch version (e.g., 2.3.1 instead of 2.3.0).
```

### Hotfix Issues

**Issue**: Hotfix branch conflicts with main
```bash
# Resolve conflicts manually
git checkout hotfix/v2.3.1
git fetch origin main
git merge origin/main
# Resolve conflicts
git add .
git commit -m "merge: resolve conflicts with main"
git push origin hotfix/v2.3.1
```

## Best Practices

1. **Always use the automated workflows** - They ensure consistency and reduce errors

2. **Test before releasing** - Never skip tests in production releases

3. **Communicate early** - Notify dependent teams before releasing

4. **Document breaking changes** - Make upgrade paths clear

5. **Keep CHANGELOG.md updated** - Document all significant changes

6. **Version bumps should be intentional**:
   - Patch (2.3.0 → 2.3.1): Bug fixes only
   - Minor (2.3.0 → 2.4.0): New features, backwards-compatible
   - Major (2.3.0 → 3.0.0): Breaking changes

7. **Hotfixes should be minimal** - Only include the critical fix

8. **Tag protection** - Consider protecting tags in GitHub to prevent accidental deletion

## Additional Resources

- [Semantic Versioning](https://semver.org/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [GitHub Packages for Maven](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)
- [Maven Versions Plugin](https://www.mojohaus.org/versions-maven-plugin/)

## Support

For questions or issues with the release process:
1. Check this documentation
2. Review GitHub Actions logs
3. Contact the izgw-core maintainers
4. Open an issue in the repository
