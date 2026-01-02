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

CURRENT_VERSION=$(grep 'ThisBuild / version' build.sbt | sed 's/.*"\(.*\)"/\1/')

if [[ ! $CURRENT_VERSION =~ -SNAPSHOT$ ]]; then
    echo_error "Current version ($CURRENT_VERSION) is not a SNAPSHOT version"
    echo_error "Snapshot releases must have a version ending with -SNAPSHOT"
    exit 1
fi

echo_info "Publishing SNAPSHOT version: $CURRENT_VERSION"

if [[ -n $(git status -s) ]]; then
    echo_warn "Working directory has uncommitted changes"
    git status -s
    read -p "Continue anyway? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo_info "Running tests..."
sbt clean +test

echo_info "Publishing snapshot to Sonatype..."
sbt +publish

echo_info "=========================================="
echo_info "Snapshot $CURRENT_VERSION published!"
echo_info "=========================================="
echo_info ""
echo_info "Snapshot repository:"
echo_info "  https://s01.oss.sonatype.org/content/repositories/snapshots/"
echo_info ""
echo_info "Add to your build.sbt:"
echo_info "  resolvers += \"Sonatype OSS Snapshots\" at \"https://s01.oss.sonatype.org/content/repositories/snapshots\""
echo_info ""
echo_info "Dependency:"
echo_info "  \"com.cloud-apim\" %% \"seclang-engine\" % \"$CURRENT_VERSION\""
