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
- Development: `X.Y.Z-izgw-core-SNAPSHOT` (e.g., `2.4.0-izgw-core-SNAPSHOT`)
- Release: `X.Y.Z-izgw-core` (e.g., `2.4.0-izgw-core`)

**Branch Strategy:**
- **develop**: Active development, contains unreleased features
- **main**: Production-ready code, reflects latest release
- **release/X.Y.Z-izgw-core**: Release preparation branches (kept after release)
- **hotfix/vX.Y.Z-izgw-core**: Emergency fixes for production releases

## Release Process

### Automated Release (Recommended)

The automated release process creates a release branch from `develop`, prepares the release, merges to `main`, and updates `develop` with the next SNAPSHOT version.

#### Prerequisites

Before starting a release, ensure:
- You are on the `develop` branch
- All intended features are merged to `develop`
- All CI/CD checks pass on `develop`
- No SNAPSHOT dependencies (except parent BOM)
- All tests pass locally

#### Step 1: Trigger the Release Workflow

1. **Go to GitHub Actions**
   - Navigate to: **Actions** → **Release**

2. **Click "Run workflow"**

3. **Fill in the parameters:**
   - **Branch**: Select `develop` (must be develop!)
   - **Release version**: The version to release (e.g., `2.4.0-izgw-core`)
   - **Next SNAPSHOT version**: The next development version (e.g., `2.5.0-izgw-core-SNAPSHOT`)
   - **Skip tests**: ❌ Leave unchecked (only for emergencies)
   - **Delete release branch on failure**: ✅ Leave checked (default)

4. **Click "Run workflow"**

#### Step 2: What the Workflow Does

The workflow automatically performs these steps:

**1. Validation Phase**
- ✅ Validates version format
- ✅ Confirms running from `develop` branch
- ✅ Checks release branch doesn't already exist
- ✅ Checks tag doesn't already exist
- ✅ Verifies no SNAPSHOT dependencies

**2. Release Branch Creation**
- ✅ Creates `release/X.Y.Z-izgw-core` branch from `develop`
- ✅ Pushes release branch to origin

**3. Prepare Release (on release branch)**
- ✅ Updates `RELEASE_NOTES.md` with release notes from commits
- ✅ Sets version to release version (removes `-SNAPSHOT`)
- ✅ Commits changes: `"chore: prepare release X.Y.Z-izgw-core"`
- ✅ Runs full test suite
- ✅ Runs OWASP dependency check

**4. Build and Deploy**
- ✅ Builds release artifacts
- ✅ Deploys to GitHub Packages

**5. Merge to Main**
- ✅ Merges release branch to `main` (no-fast-forward)
- ✅ Creates tag `vX.Y.Z-izgw-core` on `main`
- ✅ Creates GitHub Release with artifacts

**6. Update Develop**
- ✅ Merges release branch back to `develop` (for RELEASE_NOTES.md)
- ✅ Bumps `develop` version to next SNAPSHOT
- ✅ Commits: `"chore: bump version to X.Y.Z-izgw-core-SNAPSHOT"`
- ✅ Pushes `develop`

**7. Keep Release Branch**
- ✅ Release branch is **kept** for future reference

#### Step 3: Post-Release Tasks

After the workflow completes successfully:

1. **Verify the Release**
   - Check the [GitHub Release](https://github.com/IZGateway/izgw-core/releases)
   - Verify artifact in [GitHub Packages](https://github.com/IZGateway/izgw-core/packages)
   - Confirm tag exists on `main` branch

2. **Notify Consuming Applications**
   - Send notification to `izg-hub` team
   - Send notification to `izg-xform` team
   - Include release notes and upgrade instructions
   - Highlight any breaking changes

3. **Review Generated Documentation**
   - Check `RELEASE_NOTES.md` in `develop` branch
   - Verify release notes are accurate
   - Update project documentation if needed

4. **Verify Branch States**
   ```bash
   # Main should be at release version
   git checkout main
   git pull
   mvn help:evaluate -Dexpression=project.version -q -DforceStdout
   # Should show: X.Y.Z-izgw-core

   # Develop should be at next SNAPSHOT
   git checkout develop
   git pull
   mvn help:evaluate -Dexpression=project.version -q -DforceStdout
   # Should show: X.Y.Z-izgw-core-SNAPSHOT (next version)
   ```

### Release Failure Handling

If the release workflow fails:

**Default Behavior (delete-release-branch-on-failure = true):**
1. The release branch is automatically deleted
2. Fix the issues on `develop` branch
3. Re-run the workflow with the same version numbers

**Keep Branch for Investigation (delete-release-branch-on-failure = false):**
1. The release branch is kept for manual investigation
2. Investigate the release branch
3. Delete the branch manually when ready:
   ```bash
   git push origin --delete release/X.Y.Z-izgw-core
   ```
4. Fix issues on `develop`
5. Re-run the workflow

## Hotfix Process

Hotfixes are used for emergency patches to production releases without including unreleased features from `develop`.

### When to Use Hotfix

Use hotfix workflow for:
- Critical security vulnerabilities
- Production-breaking bugs
- Data corruption issues
- Urgent fixes that cannot wait for next release

### Hotfix Steps

#### Step 1: Identify Base Version

Determine which release needs the hotfix (e.g., `2.3.0-izgw-core`)

#### Step 2: Trigger Hotfix Workflow

1. Go to **Actions** → **Hotfix Release** in GitHub
2. Click **Run workflow**
3. Fill in the parameters:
   - **Base version**: The version to hotfix (e.g., `2.3.0-izgw-core`)
   - **Hotfix version**: The new patch version (e.g., `2.3.1-izgw-core`)
4. Click **Run workflow**

#### Step 3: Apply Hotfix Changes

The workflow creates a branch from the base tag. On first run:

1. Checkout the hotfix branch:
   ```bash
   git fetch origin
   git checkout hotfix/v2.3.1-izgw-core
   ```

2. Apply your fix:
   ```bash
   # Make your changes
   git add .
   git commit -m "fix: critical issue description"
   git push origin hotfix/v2.3.1-izgw-core
   ```

#### Step 4: Complete the Hotfix

1. Return to **Actions** → **Hotfix Release**
2. **Re-run** the workflow with the same parameters
3. The workflow detects changes and proceeds to:
   - Run tests
   - Build and deploy
   - Create release
   - Merge back to `main`

#### Step 5: Cherry-pick to Develop (if needed)

If the fix should be in `develop`:
```bash
git checkout develop
git cherry-pick <commit-hash>
git push origin develop
```

## Pre-Release Checklist

Before triggering a release:

### Code Quality
- [ ] All CI/CD checks pass on `develop`
- [ ] All tests pass locally
- [ ] Code review completed for all PRs
- [ ] No known critical bugs

### Dependencies
- [ ] All dependencies are on release versions (no SNAPSHOTs except parent BOM)
  ```bash
  mvn dependency:list | grep SNAPSHOT | grep -v izgw-bom
  ```
- [ ] Security vulnerabilities addressed
  ```bash
  mvn org.owasp:dependency-check-maven:check
  ```

### Version Numbers
- [ ] Release version follows semantic versioning
- [ ] Version is not already tagged
  ```bash
  git tag -l "vX.Y.Z-izgw-core"  # Should return nothing
  ```
- [ ] Next SNAPSHOT version is correctly incremented

### Communication
- [ ] Dependent teams notified of upcoming release
- [ ] Release timing coordinated
- [ ] Breaking changes documented

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

### Manual Steps

**1. Create Release Branch from Develop**
```bash
git checkout develop
git pull origin develop
git checkout -b release/2.4.0-izgw-core
git push origin release/2.4.0-izgw-core
```

**2. Update RELEASE_NOTES.md**
```bash
# Manually edit RELEASE_NOTES.md with release notes
git add RELEASE_NOTES.md
git commit -m "docs: update RELEASE_NOTES.md for release 2.4.0-izgw-core"
```

**3. Set Release Version**
```bash
mvn versions:set -DnewVersion=2.4.0-izgw-core -DgenerateBackupPoms=false
git add pom.xml
git commit -m "chore: prepare release 2.4.0-izgw-core"
git push origin release/2.4.0-izgw-core
```

**4. Run Tests and Build**
```bash
mvn clean test
mvn org.owasp:dependency-check-maven:check
mvn clean package
```

**5. Deploy to GitHub Packages**
```bash
mvn deploy -DskipTests
```

**6. Merge to Main and Tag**
```bash
git checkout main
git pull origin main
git merge --no-ff release/2.4.0-izgw-core
git push origin main

git tag -a v2.4.0-izgw-core -m "Release version 2.4.0-izgw-core"
git push origin v2.4.0-izgw-core
```

**7. Update Develop**
```bash
git checkout develop
git pull origin develop
git merge --no-ff release/2.4.0-izgw-core
mvn versions:set -DnewVersion=2.5.0-izgw-core-SNAPSHOT -DgenerateBackupPoms=false
git add pom.xml
git commit -m "chore: bump version to 2.5.0-izgw-core-SNAPSHOT"
git push origin develop
```

**8. Create GitHub Release**
- Create release manually via GitHub UI
- Attach JAR and POM files from `target/`

## Troubleshooting

### Release Workflow Failed

**Issue: Running from wrong branch**
```
Error: Release workflow must be run from 'develop' branch
Solution:
1. Switch to GitHub Actions UI
2. Select 'develop' from the branch dropdown
3. Run the workflow again
```

**Issue: SNAPSHOT dependencies detected**
```
Solution:
1. Check which dependencies are SNAPSHOTs:
   mvn dependency:list | grep SNAPSHOT | grep -v izgw-bom
2. Update dependencies to release versions in pom.xml
3. Push changes to develop
4. Re-run the release workflow
```

**Issue: Tests failed**
```
Solution:
1. Review test logs in GitHub Actions
2. Fix failing tests on develop branch
3. Push fixes to develop
4. Re-run release workflow
```

**Issue: Release branch already exists**
```
Solution:
1. Check if a previous release attempt is in progress
2. If abandoned, delete the branch:
   git push origin --delete release/X.Y.Z-izgw-core
3. Re-run the workflow
```

**Issue: Deploy to GitHub Packages failed**
```
Solution:
1. Verify GITHUB_TOKEN has write:packages permission
2. Check repository settings allow package publishing
3. Verify Maven can authenticate to GitHub Packages
4. Delete failed release branch and retry
```

### Version Conflicts

**Issue: Tag already exists**
```bash
# Check if tag exists
git ls-remote --tags origin | grep vX.Y.Z-izgw-core

# If you need to re-release (use with caution!):
git push origin --delete refs/tags/vX.Y.Z-izgw-core
git tag -d vX.Y.Z-izgw-core

# Then re-run workflow
```

**Issue: Version already in GitHub Packages**
```
Solution:
You cannot overwrite a published version in GitHub Packages.
Bump to the next patch version (e.g., 2.4.1-izgw-core instead of 2.4.0-izgw-core).
```

### Branch Issues

**Issue: Merge conflicts on main**
```bash
# This shouldn't happen in automated workflow, but if manual merge needed:
git checkout main
git pull origin main
git merge release/X.Y.Z-izgw-core
# Resolve conflicts
git add .
git commit -m "merge: resolve conflicts"
git push origin main
```

**Issue: Merge conflicts on develop**
```bash
git checkout develop
git pull origin develop
git merge release/X.Y.Z-izgw-core
# Resolve conflicts
git add .
git commit -m "merge: resolve conflicts"
git push origin develop
```

## Best Practices

1. **Always use the automated workflow** - Ensures consistency and reduces errors

2. **Test before releasing** - Never skip tests in production releases

3. **Run from develop only** - The workflow enforces this for standard releases

4. **Communicate early** - Notify dependent teams before releasing

5. **Document breaking changes** - Make upgrade paths clear for consumers

6. **Keep RELEASE_NOTES.md updated** - Workflow does this automatically

7. **Version bumps should be intentional**:
   - Patch (2.3.0 → 2.3.1): Bug fixes only
   - Minor (2.3.0 → 2.4.0): New features, backwards-compatible
   - Major (2.3.0 → 3.0.0): Breaking changes

8. **Release branches are kept** - Don't delete them, they provide history

9. **Hotfixes should be minimal** - Only include the critical fix

10. **Review generated RELEASE_NOTES** - Edit manually if needed before merging

## Workflow Diagram

```
develop (2.4.0-SNAPSHOT)
   |
   | [Trigger Release Workflow]
   |
   +---> release/2.4.0-izgw-core
   |       |
   |       | - Update RELEASE_NOTES.md
   |       | - Set version to 2.4.0-izgw-core
   |       | - Run tests
   |       | - Build & deploy
   |       |
   |       +---> main (merge)
   |       |       |
   |       |       +---> tag: v2.4.0-izgw-core
   |       |       |
   |       |       +---> GitHub Release
   |       |
   |       +---> develop (merge back)
   |               |
   |               +---> Bump to 2.5.0-izgw-core-SNAPSHOT
   |
   | (release branch kept)
```

## Additional Resources

- [Semantic Versioning](https://semver.org/)
- [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [GitHub Packages for Maven](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)
- [Maven Versions Plugin](https://www.mojohaus.org/versions-maven-plugin/)

## Support

For questions or issues with the release process:
1. Check this documentation
2. Review GitHub Actions logs
3. Contact the izgw-core maintainers
4. Open an issue in the repository
