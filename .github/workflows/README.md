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

**Purpose**: Automated release process for creating stable releases from the `develop` branch

**Required Inputs**:
- `release-version`: Version to release (format: `X.Y.Z-izgw-core`, e.g., `2.3.0-izgw-core`)
- `next-snapshot-version`: Next development version (format: `X.Y.Z-izgw-core-SNAPSHOT`, e.g., `2.4.0-izgw-core-SNAPSHOT`)

**Optional Inputs**:
- `skip-tests`: Skip tests (default: `false`, use only for emergency releases)
- `delete-release-branch-on-failure`: Delete release branch if workflow fails (default: `true`)

**Process Overview**:

The release workflow automates the entire release process, ensuring consistency and reducing manual errors. It must be run from the `develop` branch.

**Detailed Steps**:

**1. Validation Phase**
- Validates version formats (X.Y.Z-izgw-core and X.Y.Z-izgw-core-SNAPSHOT)
- Confirms running from `develop` branch
- Checks that release branch doesn't already exist
- Checks that tag doesn't already exist
- Verifies artifact doesn't exist in GitHub Packages
- Checks for SNAPSHOT dependencies (fails if found, except izgw-bom parent)

**2. Testing Phase** (unless skip-tests enabled)
- Runs full test suite (`mvn clean test`)
- Runs OWASP dependency check with CVSS threshold of 7
- Uploads dependency check report as artifact

**3. Release Branch Creation**
- Creates `release/X.Y.Z-izgw-core` branch from `develop`
- Pushes release branch to origin

**4. Release Preparation** (on release branch)
- Updates `RELEASE_NOTES.md`:
  - Identifies previous release tag
  - Extracts merged PR information from git history
  - Generates changelog with PR titles and links
  - Falls back to commit messages if no PRs found
  - Adds new release section with current date
- Sets version to release version (removes `-SNAPSHOT` suffix)
- Commits changes: `"docs: update RELEASE_NOTES.md for release X.Y.Z-izgw-core"` and `"chore: prepare release X.Y.Z-izgw-core"`
- Pushes commits to release branch

**5. Build Artifacts**
- Builds release artifacts: `mvn clean package -DskipTests -DskipDependencyCheck`

**6. Merge to Main**
- Fetches and checks out `main` branch (creates it if this is the first release)
- Merges release branch using no-fast-forward merge with theirs strategy for conflicts
  - The `theirs` strategy ensures release branch documentation (like RELEASE_NOTES.md) takes precedence
- Pushes merged changes to `main`

**7. Create Git Tag**
- Creates annotated tag `vX.Y.Z-izgw-core` on `main` branch
- Pushes tag to origin

**8. Deploy to GitHub Packages**
- Deploys artifacts to GitHub Packages: `mvn deploy -DskipTests -DskipDependencyCheck`
- Artifacts include JAR and POM files

**9. Create GitHub Release**
- Generates release notes from merged PRs between previous tag and current release
- Creates GitHub Release with:
  - Tag: `vX.Y.Z-izgw-core`
  - Title: `IZ Gateway Core vX.Y.Z-izgw-core`
  - Release notes including changes and installation instructions
  - Attached artifacts (JAR and POM files)
- Enables auto-generated release notes as well

**10. Update Develop Branch**
- Discards any local changes from main branch context
- Checks out fresh `develop` branch
- Merges release branch to `develop` (to bring in RELEASE_NOTES.md updates)
- Bumps version to next SNAPSHOT version
- Commits: `"chore: bump version to X.Y.Z-izgw-core-SNAPSHOT"`
- Pushes updated `develop` branch

**11. Keep Release Branch**
- Release branch is **kept** for historical reference and traceability

**Failure Handling**:

If `delete-release-branch-on-failure` is `true` (default):
- Automatically cleans up on failure:
  - Deletes git tag (if created)
  - Reverts main branch merge commit (if created)
  - Deletes release branch
  - Deletes GitHub Packages artifact (if deployed)
  - Deletes GitHub Release (if created)
- Provides troubleshooting summary

If `delete-release-branch-on-failure` is `false`:
- Keeps release branch for investigation
- Provides manual cleanup instructions

**Usage**:
```
Go to Actions → Release → Run workflow
Select branch: develop
Enter release-version: 2.3.0Enter next-snapshot-version: 2.4.0-izgw-core-SNAPSHOT
Leave skip-tests unchecked
Leave delete-release-branch-on-failure checked
Click: Run workflow
```

**Outputs**:
- Git tag: `v2.3.0-izgw-core` on `main` branch
- GitHub Release with release notes and artifacts
- Artifacts deployed to GitHub Packages
- Release branch: `release/2.3.0-izgw-core` (kept)
- Updated `develop` branch with next SNAPSHOT version
- Updated `RELEASE_NOTES.md` in both `main` and `develop`

**Post-Release Actions**:
1. Review the GitHub Release
2. **Notify consuming applications** (izg-hub, izg-xform) of the new release
3. Review RELEASE_NOTES.md in develop branch
4. Update integration documentation if needed

**See**: [RELEASING.md](../../RELEASING.md) for detailed release instructions and best practices

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
- **DO** run on a clean state (all PRs merged to develop)
- **DO** run from the `develop` branch only
- **DO** verify tests pass before releasing
- **DO** check for SNAPSHOT dependencies before releasing
- **DO** use semantic versioning correctly
- **DON'T** skip tests unless it's an emergency
- **DON'T** release with known bugs
- **DON'T** release with SNAPSHOT dependencies

### 2. Maven CI Workflow
- **DO** let it run on all PRs
- **DO** fix failing checks before merging
- **DO** review dependency check reports
- **DON'T** merge with failing tests
- **DON'T** ignore security vulnerabilities

---

## Troubleshooting

### Workflow Failed - Wrong Branch
```
Error: Release workflow must be run from 'develop' branch
```
**Solution**: In GitHub Actions UI, select `develop` from the branch dropdown before running the workflow

### Workflow Failed - SNAPSHOT Dependencies
```
Error: Found SNAPSHOT dependencies
```
**Solution**: Update all dependencies to release versions in pom.xml (except izgw-bom parent)

### Workflow Failed - Tests
```
Error: Tests failed
```
**Solution**: Fix failing tests on the develop branch, push changes, re-run workflow

### Tag Already Exists
```
Error: Tag v2.3.0-izgw-core already exists
```
**Solution**: Use a different version number or delete the existing tag (with extreme caution)

### Artifact Already Exists
```
Error: Artifact version already exists in GitHub Packages
```
**Solution**:
1. Delete the package version from GitHub Packages (see workflow error message for commands)
2. Or use a different version number

### Version Validation Failed
```
Error: Version must be in format X.Y.Z```
**Solution**: Ensure version follows the required format:
- Release: `2.3.0-izgw-core` (no SNAPSHOT)
- Next SNAPSHOT: `2.4.0-izgw-core-SNAPSHOT`

### Authentication Error
```
Error: Failed to deploy to GitHub Packages
```
**Solution**:
1. Check GITHUB_TOKEN permissions in repository settings
2. Verify Maven settings configuration
3. Ensure GitHub Packages is enabled for the repository

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
- [RELEASING.md](../../RELEASING.md) - Detailed release guide

---

## Questions?

For questions about workflows:
1. Check [RELEASING.md](../../RELEASING.md)
2. Review workflow logs in Actions tab
3. Contact izgw-core maintainers
4. Open an issue in the repository
