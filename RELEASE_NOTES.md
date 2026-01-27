## [2.28.0] - 2026-01-27

### Changes
- chore: update release workflow to include install step and remove duplicate failure notification ([#68](https://github.com/austinmoody/izgw-core/pull/68))

## [2.27.0] - 2026-01-22

### Changes
- Combine cleanup and notification failure steps ([#67](https://github.com/austinmoody/izgw-core/pull/67))

## [2.26.0] - 2026-01-22

### Changes
- chore: fix XML encoding and improve dependency filtering in release workflow ([#66](https://github.com/austinmoody/izgw-core/pull/66))

## [2.25.0] - 2026-01-21

### Changes
- Add missing maven setup ([#65](https://github.com/austinmoody/izgw-core/pull/65))

## [2.24.0] - 2026-01-20

### Changes
- Add site command to mvn ([#64](https://github.com/austinmoody/izgw-core/pull/64))
- Add publish to workflow ([#63](https://github.com/austinmoody/izgw-core/pull/63))

## [2.23.0] - 2026-01-15

### Changes
- chore: update permissions in release and hotfix workflows ([#62](https://github.com/austinmoody/izgw-core/pull/62))
- chore: add explicit permissions to release, hotfix, and common workflows ([#61](https://github.com/austinmoody/izgw-core/pull/61))
- docs: reword LogControllerBase comments for clarity and conciseness ([#59](https://github.com/austinmoody/izgw-core/pull/59))

## [2.22.1] - 2026-01-14

### Changes
- docs: rewrite LogControllerBase comments for clarity and emphasis on blacklisting loophole ([#60](https://github.com/austinmoody/izgw-core/pull/60))

## [2.22.0] - 2026-01-14

### Changes
- docs: clarify hotfix merge verification steps in release process ([#58](https://github.com/austinmoody/izgw-core/pull/58))

## [2.21.1] - 2026-01-14

### Changes
- docs: simplify release documentation and add hotfix merge verificatio… ([#57](https://github.com/austinmoody/izgw-core/pull/57))

## [2.21.0] - 2026-01-14

### Changes
- chore: remove ability to skip tests and OWASP check from release workflows ([#56](https://github.com/austinmoody/izgw-core/pull/56))

## [2.20.0] - 2026-01-14

### Changes
- chore: remove OWASP report directory before develop checkout in release workflow ([#55](https://github.com/austinmoody/izgw-core/pull/55))
- chore: fix indentation for OWASP dependency check and report upload s… ([#54](https://github.com/austinmoody/izgw-core/pull/54))
- chore: migrate OWASP dependency check to GitHub Action in release wor… ([#53](https://github.com/austinmoody/izgw-core/pull/53))

## [2.19.0] - 2026-01-13

### Changes
- chore: remove unused secrets section from release workflow inputs. Secrets are inherited now not passed in ([#52](https://github.com/austinmoody/izgw-core/pull/52))
- refactor: combine Maven test, build, deploy, and site generation steps into a single job in release workflow ([#51](https://github.com/austinmoody/izgw-core/pull/51))
- Remove delete-release-branch-on-failure option from release workflows and clean up related logic ([#50](https://github.com/austinmoody/izgw-core/pull/50))
- Rename release workflow file to _release_common.yml and update references in hotfix and release workflows ([#49](https://github.com/austinmoody/izgw-core/pull/49))
- Update release workflow to use JDK 21 clean up some verbose log messages ([#48](https://github.com/austinmoody/izgw-core/pull/48))

## [2.18.0] - 2026-01-13

### Changes
- Rename release workflow file to _release_common.yml and update references in hotfix and release workflows ([#49](https://github.com/austinmoody/izgw-core/pull/49))
- Update release workflow to use JDK 21 clean up some verbose log messages ([#48](https://github.com/austinmoody/izgw-core/pull/48))

## [2.17.1] - 2025-12-29

### Changes
- docs: update release documentation for simplified version specification (b1732d4)
- chore: bump version to 2.18.0-SNAPSHOT (c0d3cc0)

## [2.17.0] - 2025-12-29

### Changes
- feat: simplify release workflow versioning ([#44](https://github.com/austinmoody/izgw-core/pull/44))
- Renamed some of the workflows and tweaked README. ([#43](https://github.com/austinmoody/izgw-core/pull/43))
- docs: update workflow documentation to fully reflect release and hotf… ([#42](https://github.com/austinmoody/izgw-core/pull/42))

## [2.16.1] - 2025-12-29

### Changes
- docs: update to trigger hotfix attempt (c08fab9)

## [2.16.0] - 2025-12-29

### Changes
- fix: improve cleanup logging and error handling for failed releases ([#41](https://github.com/austinmoody/izgw-core/pull/41))
- fix: preserve PR_CHANGES.txt during develop branch update ([#40](https://github.com/austinmoody/izgw-core/pull/40))
- fix: reorder release workflow to prevent premature GitHub Release cre… ([#39](https://github.com/austinmoody/izgw-core/pull/39))
- fix: add merge strategy to resolve hotfix-to-develop pom.xml conflicts ([#38](https://github.com/austinmoody/izgw-core/pull/38))

## [2.15.0] - 2025-12-29

### Changes
- feat: add hotfix release workflow ([#37](https://github.com/austinmoody/izgw-core/pull/37))
- docs: update release documentation for reusable workflow ([#36](https://github.com/austinmoody/izgw-core/pull/36))

## [2.14.0] - 2025-12-28

### Changes
- refactor: extract release logic into reusable workflow ([#35](https://github.com/austinmoody/izgw-core/pull/35))
- fix: remove invalid 'branches' option from workflow_dispatch ([#34](https://github.com/austinmoody/izgw-core/pull/34))

## [2.13.0] - 2025-12-27

### Changes
- feat: make OWASP dependency check optional in release workflow ([#33](https://github.com/austinmoody/izgw-core/pull/33))
- chore: restrict release workflow to develop branch only ([#32](https://github.com/austinmoody/izgw-core/pull/32))

## [2.12.0] - 2025-12-27

### Changes
- Tweak dependency check for SNAPSHOT to not take into account the current version of this project itself. ([#31](https://github.com/austinmoody/izgw-core/pull/31))
- fix: check parent POM version in SNAPSHOT dependency validation ([#30](https://github.com/austinmoody/izgw-core/pull/30))
- fix: update SNAPSHOT dependency check to use dependency:list ([#29](https://github.com/austinmoody/izgw-core/pull/29))
- docs: update release branch naming to match workflow implementation ([#28](https://github.com/austinmoody/izgw-core/pull/28))
- refactor: remove -izgw-core suffix from version naming ([#27](https://github.com/austinmoody/izgw-core/pull/27))

## [2.11.0-izgw-core] - 2025-12-27

### Changes
- refactor: synchronize release notes between RELEASE_NOTES.md and GitHub Releases ([#26](https://github.com/austinmoody/izgw-core/pull/26))

## [2.10.0-izgw-core] - 2025-12-15

### Changes
- docs: remove hotfix workflow and update documentation ([#25](https://github.com/austinmoody/izgw-core/pull/25))

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
