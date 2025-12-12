## [2.9.0-izgw-core] - 2025-12-12

### Changes
- refactor: run OWASP scanner and tests on develop before creating rele… ([#24](https://github.com/austinmoody/izgw-core/pull/24))
- refactor: consolidate artifact existence check into prerequisites val… ([#23](https://github.com/austinmoody/izgw-core/pull/23))
- fix: use version-sorted tag list instead of git describe for PREVIOUS… ([#22](https://github.com/austinmoody/izgw-core/pull/22))

## [2.8.0-izgw-core] - 2025-12-06

### Changes
- docs: update release documentation and remove emojis ([#21](https://github.com/austinmoody/izgw-core/pull/21))
- fix: comprehensive fixes for develop update and failure cleanup ([#20](https://github.com/austinmoody/izgw-core/pull/20))
- feat: add fail-fast validation for existing artifacts ([#19](https://github.com/austinmoody/izgw-core/pull/19))
- fix: reorder release workflow to prevent orphaned artifacts ([#18](https://github.com/austinmoody/izgw-core/pull/18))
- Update tomcat and netty versions for dependency check ([#17](https://github.com/austinmoody/izgw-core/pull/17))
- fix: prevent release deployment when OWASP dependency check fails ([#16](https://github.com/austinmoody/izgw-core/pull/16))
- Rename CHANGELOG.md to RELEASE_NOTES.md and update workflows (hopeful… ([#14](https://github.com/austinmoody/izgw-core/pull/14))
- Fix CHANGELOG.md references in hotfix.yml workflow ([#15](https://github.com/austinmoody/izgw-core/pull/15))
- IGDD-3939 - Add database url sanitation for HealthService ([#13](https://github.com/austinmoody/izgw-core/pull/13))
- IGDD-1234 - update release and hotfix workflows to use PR titles for change log ([#12](https://github.com/austinmoody/izgw-core/pull/12))
- feat: implement release branch strategy for standard releases and hotfixes ([#11](https://github.com/austinmoody/izgw-core/pull/11))
- Update hotfix workflow to use -izgw-core version format ([#10](https://github.com/austinmoody/izgw-core/pull/10))

## [2.7.0-izgw-core] - 2025-12-06

### Changes
- fix: comprehensive fixes for develop update and failure cleanup ([#20](https://github.com/austinmoody/izgw-core/pull/20))
- feat: add fail-fast validation for existing artifacts ([#19](https://github.com/austinmoody/izgw-core/pull/19))
- fix: reorder release workflow to prevent orphaned artifacts ([#18](https://github.com/austinmoody/izgw-core/pull/18))
- Update tomcat and netty versions for dependency check ([#17](https://github.com/austinmoody/izgw-core/pull/17))
- fix: prevent release deployment when OWASP dependency check fails ([#16](https://github.com/austinmoody/izgw-core/pull/16))
- Rename CHANGELOG.md to RELEASE_NOTES.md and update workflows (hopeful… ([#14](https://github.com/austinmoody/izgw-core/pull/14))
- Fix CHANGELOG.md references in hotfix.yml workflow ([#15](https://github.com/austinmoody/izgw-core/pull/15))
- IGDD-3939 - Add database url sanitation for HealthService ([#13](https://github.com/austinmoody/izgw-core/pull/13))
- IGDD-1234 - update release and hotfix workflows to use PR titles for change log ([#12](https://github.com/austinmoody/izgw-core/pull/12))
- feat: implement release branch strategy for standard releases and hotfixes ([#11](https://github.com/austinmoody/izgw-core/pull/11))
- Update hotfix workflow to use -izgw-core version format ([#10](https://github.com/austinmoody/izgw-core/pull/10))

## [2.6.0-izgw-core] - 2025-11-20

### Changes
- IGDD-3939 - Add database url sanitation for HealthService ([#13](https://github.com/austinmoody/izgw-core/pull/13))
- IGDD-1234 - update release and hotfix workflows to use PR titles for change log ([#12](https://github.com/austinmoody/izgw-core/pull/12))
- feat: implement release branch strategy for standard releases and hotfixes ([#11](https://github.com/austinmoody/izgw-core/pull/11))
- Update hotfix workflow to use -izgw-core version format ([#10](https://github.com/austinmoody/izgw-core/pull/10))

## [2.5.0-izgw-core] - 2025-11-19

### Changes
- Update parent version from 1.0.1-SNAPSHOT to 1.0.2-RELEASE (e44eee0)
- feat: implement release branch strategy for standard releases and hotfixes (55f3dee)
- feat: update hotfix workflow to use -izgw-core version format (eaf74d1)
- docs: fix version format discrepancies in release documentation (5e1b039)
- chore: bump version to 2.5.0-izgw-core-SNAPSHOT (8b64b41)

# Release Notes

All notable changes to the IZ Gateway Core library will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Release automation workflows (release.yml, hotfix.yml)
- Maven enforcer plugin configuration for SNAPSHOT dependency checking
- Comprehensive release documentation (RELEASING.md)
- Changelog template

### Changed

### Deprecated

### Removed

### Fixed

### Security

---

## [2.3.0-SNAPSHOT] - In Development

### Added
- RevocationChecker enhancements to handle exceptions during certificate status validation
- Improved error reporting in logs for better diagnostics

### Changed
- Refactored POM to use izgw-bom for dependency management
- Updated classpath configuration and removed unused entries

### Fixed
- DEX Send Failures (IGDD-2267)
- JWT shared secret now properly treated as base64 encoded data (IGDD-2052)

---

## Release Template

Use this template when preparing release notes:

```markdown
## [X.Y.Z] - YYYY-MM-DD

### Added
- New features, capabilities, or APIs
- New dependencies or tools

### Changed
- Changes to existing functionality
- Dependency version updates
- Configuration changes

### Deprecated
- Features that are being phased out
- APIs that will be removed in future versions

### Removed
- Removed features or APIs
- Removed dependencies

### Fixed
- Bug fixes
- Performance improvements
- Error handling improvements

### Security
- Security patches
- Vulnerability fixes
- Dependency security updates
```

---

## Version History

<!-- Releases will be documented here in reverse chronological order -->

## How to Use This Changelog

### For Maintainers

1. **During Development**: Add changes to the `[Unreleased]` section as you work
2. **Before Release**: Move unreleased changes to a new version section
3. **Release Format**: `## [X.Y.Z] - YYYY-MM-DD`
4. **Link the version**: Add comparison links at the bottom of the file

### For Users

- **Breaking Changes**: Look in `Changed` or `Removed` sections
- **New Features**: Check the `Added` section
- **Bug Fixes**: Review the `Fixed` section
- **Security**: Always review the `Security` section

### Categories Explained

- **Added**: New features that didn't exist before
- **Changed**: Modifications to existing features
- **Deprecated**: Features still available but planned for removal
- **Removed**: Features that have been deleted
- **Fixed**: Bug fixes and error corrections
- **Security**: Security-related changes (always important!)

### Examples

**Added**
```markdown
- Support for FHIR R4 message parsing
- New configuration option for connection timeouts: `izgw.timeout.connect`
- Integration with AWS Secrets Manager for credential storage
```

**Changed**
```markdown
- Improved performance of message validation (50% faster)
- Updated Spring Boot from 3.5.3 to 3.5.4
- Changed default log level from DEBUG to INFO
```

**Deprecated**
```markdown
- `LegacyMessageParser` will be removed in v3.0.0, use `MessageParserV2` instead
- Configuration property `old.setting` deprecated in favor of `new.setting`
```

**Removed**
```markdown
- Removed deprecated `processMessageOld()` method (use `processMessage()`)
- Dropped support for Java 11
```

**Fixed**
```markdown
- Fixed NullPointerException when processing empty messages (#123)
- Corrected timezone handling in date parsing
- Resolved memory leak in connection pooling
```

**Security**
```markdown
- Updated log4j to address CVE-2021-44228
- Fixed SQL injection vulnerability in query builder
- Patched XML external entity (XXE) vulnerability
```

---

## Semantic Versioning Quick Reference

Given a version number MAJOR.MINOR.PATCH (e.g., 2.3.1):

- **MAJOR** (2.x.x): Incompatible API changes
  - Breaking changes
  - Removed features
  - Significant architectural changes

- **MINOR** (x.3.x): New functionality (backwards-compatible)
  - New features
  - New APIs
  - Deprecations

- **PATCH** (x.x.1): Backwards-compatible bug fixes
  - Bug fixes
  - Performance improvements
  - Security patches (non-breaking)

---

## Links

- [Release Documentation](./RELEASING.md)
- [GitHub Releases](https://github.com/IZGateway/izgw-core/releases)
- [GitHub Packages](https://github.com/IZGateway/izgw-core/packages)

<!-- Version comparison links will be added here -->
<!--
[Unreleased]: https://github.com/IZGateway/izgw-core/compare/v2.3.0...HEAD
[2.3.0]: https://github.com/IZGateway/izgw-core/compare/v2.2.0...v2.3.0
-->
