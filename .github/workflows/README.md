# GitHub Actions Workflows

This directory contains the GitHub Actions workflows for izgw-core.

## Workflows

### 1. Maven CI (`maven.yml`)

**Trigger**: Push or PR to `Release*`, `main`, or `develop` branches

**Purpose**: Continuous Integration - builds, tests, and validates code changes

**Key Steps**:
- Sets up JDK 17 and Maven
- Configures Maven settings with GitHub Packages authentication
- Runs tests with coverage
- Runs OWASP dependency check (on main branch)
- Deploys SNAPSHOT artifacts to GitHub Packages
- Creates draft releases (on push to main)

**Environment Variables**:
- `GITHUB_TOKEN`: GitHub authentication
- `COMMON_PASS`: Common password for tests
- `ELASTIC_API_KEY`: Elasticsearch API key for tests

---

### 2. Release (`release.yml`)

**Trigger**: Manual (`workflow_dispatch`)

**Purpose**: Automated release process for creating stable releases

**Required Inputs**:
- `release-version`: Version to release (e.g., `2.3.0-izgw-core`)
- `next-snapshot-version`: Next development version (e.g., `2.4.0-izgw-core-SNAPSHOT`)
- `skip-tests`: Optional - skip tests (emergency only)

**Process**:
1. Validates version formats and prerequisites
2. Checks for SNAPSHOT dependencies (fails if found, except parent BOM)
3. Checks if artifact already exists in GitHub Packages
4. Creates release branch from develop
5. Updates RELEASE_NOTES.md with release notes from merged PRs
6. Updates pom.xml to release version
7. Runs full test suite (unless skip-tests enabled)
8. Runs OWASP dependency check (unless skip-tests enabled)
9. Builds and packages artifacts
10. Merges release branch to main
11. Creates Git tag (`vX.Y.Z-izgw-core`) on main
12. Deploys to GitHub Packages
13. Generates release notes and creates GitHub Release with artifacts
14. Merges release branch back to develop and bumps version to next SNAPSHOT
15. Keeps release branch for history

**Usage**:
```
Go to Actions → Release → Run workflow
Enter: release-version: 2.3.0-izgw-core
Enter: next-snapshot-version: 2.4.0-izgw-core-SNAPSHOT
Click: Run workflow
```

**Outputs**:
- Git tag (e.g., `v2.3.0-izgw-core`)
- GitHub Release with release notes
- Artifacts in GitHub Packages
- Updated pom.xml with next SNAPSHOT version

**See**: [RELEASING.md](../../RELEASING.md) for detailed instructions

---

### 3. Hotfix Release (`hotfix.yml`)

**Trigger**: Manual (`workflow_dispatch`)

**Purpose**: Emergency patch releases for production issues

**Required Inputs**:
- `base-version`: Version to hotfix (e.g., `2.3.0-izgw-core`)
- `hotfix-version`: New patch version (e.g., `2.3.1-izgw-core`)

**Process**:

**First Run** (Creates hotfix branch):
1. Validates versions
2. Checks base tag exists
3. Creates hotfix branch from base tag
4. Pauses for manual fix application

**Second Run** (Completes release):
1. Checks for SNAPSHOT dependencies
2. Runs tests
3. Updates version to hotfix version
4. Builds and deploys
5. Creates Git tag
6. Creates GitHub Release
7. Merges back to main

**Usage**:
```
# First run - create hotfix branch
Go to Actions → Hotfix Release → Run workflow
Enter: base-version: 2.3.0-izgw-core
Enter: hotfix-version: 2.3.1-izgw-core
Click: Run workflow

# Apply your fix
git checkout hotfix/v2.3.1-izgw-core
# Make changes
git add .
git commit -m "fix: critical issue"
git push origin hotfix/v2.3.1-izgw-core

# Second run - complete release
Go to Actions → Hotfix Release → Run workflow (same inputs)
```

**Validation**:
- Hotfix must be same major.minor as base (2.3.x only)
- Hotfix patch must be greater than base patch
- Base tag must exist
- Both versions must include `-izgw-core` suffix

**See**: [RELEASING.md](../../RELEASING.md#hotfix-process) for detailed instructions

---

## Workflow Security

### Secrets Required

- `GITHUB_TOKEN`: Automatically provided by GitHub Actions
  - Used for: GitHub Packages deployment, creating releases, pushing tags

- `ACTIONS_KEY`: SSH key for pushing to protected branches
  - Used for: Committing version changes
  - Setup: Repository Settings → Secrets → Actions

- `COMMON_PASS`: Password for test environment
  - Used for: Running integration tests

- `ELASTIC_API_KEY`: Elasticsearch API key
  - Used for: Testing Elasticsearch integration

### Permissions

Workflows require the following permissions:
- `contents: write` - Create tags, releases, push commits
- `packages: write` - Deploy to GitHub Packages
- `actions: read` - Access workflow runs

---

## Workflow Best Practices

### 1. Release Workflow
- **DO** run on a clean state (all PRs merged)
- **DO** verify tests pass before releasing
- **DO** check for SNAPSHOT dependencies
- **DON'T** skip tests unless emergency
- **DON'T** release with known bugs

### 2. Hotfix Workflow
- **DO** use for critical production issues only
- **DO** keep changes minimal (single fix)
- **DO** cherry-pick to develop branch after
- **DON'T** add new features in hotfix
- **DON'T** combine multiple fixes

### 3. Maven CI Workflow
- **DO** let it run on all PRs
- **DO** fix failing checks before merging
- **DO** review dependency check reports
- **DON'T** merge with failing tests
- **DON'T** ignore security vulnerabilities

---

## Troubleshooting

### Workflow Failed - Authentication Error
```
Error: Failed to deploy to GitHub Packages
```
**Solution**: Check GITHUB_TOKEN permissions and Maven settings configuration

### Workflow Failed - SNAPSHOT Dependencies
```
Error: Found SNAPSHOT dependencies
```
**Solution**: Update all dependencies to release versions in pom.xml

### Workflow Failed - Tests
```
Error: Tests failed
```
**Solution**: Fix failing tests on the branch, push changes, re-run workflow

### Tag Already Exists
```
Error: Tag v2.3.0-izgw-core already exists
```
**Solution**: Use a different version number or delete the existing tag (with caution)

### Version Validation Failed
```
Error: Version must be in format X.Y.Z-izgw-core
```
**Solution**: Ensure version follows the required format (e.g., 2.3.0-izgw-core for releases, 2.3.1-izgw-core for hotfixes)

---

## Workflow Maintenance

### Updating Workflows

1. Test changes in a feature branch first
2. Use workflow validation tools
3. Document changes in pull request
4. Consider backward compatibility

### Monitoring

- Review workflow runs regularly in Actions tab
- Check for deprecated actions/features
- Monitor execution times
- Review artifact sizes

### Dependencies

Workflows use these GitHub Actions:
- `actions/checkout@v4` - Checkout repository
- `actions/setup-java@v4` - Setup JDK
- `stCarolas/setup-maven@v5` - Setup Maven
- `actions/upload-artifact@v4` - Upload build artifacts
- `softprops/action-gh-release@v2` - Create GitHub releases

---

## Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Maven GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)
- [Semantic Versioning](https://semver.org/)
- [Keep a Changelog](https://keepachangelog.com/)

---

## Questions?

For questions about workflows:
1. Check [RELEASING.md](../../RELEASING.md)
2. Review workflow logs in Actions tab
3. Contact izgw-core maintainers
4. Open an issue in the repository
