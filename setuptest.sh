#!/bin/sh

BRANCH="main"

if [ -d "test-data/coreruleset" ]; then
  cd test-data/coreruleset
  git checkout $BRANCH
  git pull origin $BRANCH
else
  git clone --depth=1 --branch $BRANCH https://github.com/coreruleset/coreruleset.git test-data/coreruleset
  git checkout $BRANCH
fi
