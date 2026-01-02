# Publishing to Maven Central

This document describes how to publish the seclang-engine library to Maven Central.

## Prerequisites

1. **Sonatype Account**: Create an account at https://s01.oss.sonatype.org/
2. **Group ID Verification**: Verify ownership of `com.cloud-apim` group ID via Sonatype JIRA
3. **GPG Key**: Generate and publish a GPG key for signing artifacts

### Setting up GPG

```bash
# Generate a GPG key
gpg --gen-key

# List keys to get the key ID
gpg --list-keys

# Publish the key to a key server
gpg --keyserver keyserver.ubuntu.com --send-keys <YOUR_KEY_ID>
```

### Configuring Credentials

Create or edit `~/.sbt/1.0/sonatype.sbt`:

```scala
credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "s01.oss.sonatype.org",
  "<your-sonatype-username>",
  "<your-sonatype-password>"
)
```

Create or edit `~/.sbt/1.0/gpg.sbt`:

```scala
// Option 1: Use gpg-agent (recommended)
useGpgAgent := true

// Option 2: Provide passphrase directly (less secure)
// pgpPassphrase := Some("<your-gpg-passphrase>".toCharArray)
```

## Publishing Process

### 1. Update Version

Edit `build.sbt` and update the version:

```scala
ThisBuild / version := "1.0.0"  // Remove -dev suffix for release
```

### 2. Cross-compile and Test

```bash
sbt clean
sbt +test
```

### 3. Publish to Sonatype

```bash
# Publish signed artifacts to staging repository
sbt +publishSigned

# Release to Maven Central
sbt sonatypeBundleRelease
```

Alternatively, use the combined command:

```bash
sbt +publishSigned sonatypeBundleRelease
```

### 4. Verify Publication

After 10-30 minutes, verify the artifact is available:
- Staging: https://s01.oss.sonatype.org/content/repositories/releases/
- Maven Central: https://repo1.maven.org/maven2/com/cloud-apim/seclang-engine_2.12/

### 5. Tag the Release

```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

## Snapshot Releases

For snapshot releases:

```scala
ThisBuild / version := "1.1.0-SNAPSHOT"
```

Then publish:

```bash
sbt +publish
```

Snapshots are available at: https://s01.oss.sonatype.org/content/repositories/snapshots/

## Troubleshooting

### GPG Issues

If you encounter GPG signing issues:

```bash
# Export GPG_TTY
export GPG_TTY=$(tty)

# Test GPG
echo "test" | gpg --clearsign
```

### Sonatype Credentials

Verify credentials are correctly set:

```bash
cat ~/.sbt/1.0/sonatype.sbt
```

### Repository Not Found

Ensure your Sonatype account has been approved and the group ID `com.cloud-apim` is verified.

## Additional Resources

- [Sonatype OSSRH Guide](https://central.sonatype.org/publish/publish-guide/)
- [sbt-sonatype Plugin](https://github.com/xerial/sbt-sonatype)
- [sbt-pgp Plugin](https://github.com/sbt/sbt-pgp)
