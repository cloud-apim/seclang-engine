#!/bin/bash

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

echo_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

echo_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

if [ $# -eq 0 ]; then
    echo_error "Usage: ./release.sh <version>"
    echo_error "Example: ./release.sh 1.0.0"
    exit 1
fi

VERSION=$1
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

if [ "$CURRENT_BRANCH" != "main" ] && [ "$CURRENT_BRANCH" != "master" ]; then
    echo_warn "You are not on main/master branch (current: $CURRENT_BRANCH)"
    read -p "Continue anyway? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

if [[ ! $VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo_error "Version must follow semantic versioning (e.g., 1.0.0)"
    exit 1
fi

echo_info "Starting release process for version $VERSION"

if [[ -n $(git status -s) ]]; then
    echo_error "Working directory is not clean. Commit or stash your changes first."
    git status -s
    exit 1
fi

echo_info "Fetching latest changes from remote..."
git fetch

echo_info "Updating version in build.sbt..."
sed -i.bak "s/ThisBuild \/ version.*:= \".*\"/ThisBuild \/ version          := \"$VERSION\"/" build.sbt
rm -f build.sbt.bak

echo_info "Running tests..."
sbt clean +test

echo_info "Building documentation..."
sbt +doc

echo_info "Committing version bump..."
git add build.sbt
git commit -m "Release version $VERSION"

echo_info "Creating git tag v$VERSION..."
git tag -a "v$VERSION" -m "Release version $VERSION"

echo_info "Publishing signed artifacts to Sonatype..."
sbt +publishSigned

echo_info "Releasing to Maven Central..."
sbt sonatypeBundleRelease

echo_info "Pushing changes to remote..."
git push origin "$CURRENT_BRANCH"
git push origin "v$VERSION"

NEXT_VERSION="${VERSION%.*}.$((${VERSION##*.}+1))-SNAPSHOT"
echo_info "Updating to next development version: $NEXT_VERSION"
sed -i.bak "s/ThisBuild \/ version.*:= \".*\"/ThisBuild \/ version          := \"$NEXT_VERSION\"/" build.sbt
rm -f build.sbt.bak

git add build.sbt
git commit -m "Prepare next development iteration $NEXT_VERSION"
git push origin "$CURRENT_BRANCH"

echo_info "=========================================="
echo_info "Release $VERSION completed successfully!"
echo_info "=========================================="
echo_info ""
echo_info "Next steps:"
echo_info "1. Wait 10-30 minutes for sync to Maven Central"
echo_info "2. Verify at: https://repo1.maven.org/maven2/com/cloud-apim/seclang-engine_2.12/$VERSION/"
echo_info "3. Create GitHub release at: https://github.com/cloud-apim/seclang-engine/releases/new?tag=v$VERSION"
echo_info ""
echo_info "Artifact coordinates:"
echo_info "  groupId: com.cloud-apim"
echo_info "  artifactId: seclang-engine_2.12 (or seclang-engine_2.13)"
echo_info "  version: $VERSION"
