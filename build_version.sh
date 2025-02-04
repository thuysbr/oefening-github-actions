#!/bin/bash

# build_version.sh will return a build version based upon the commit history.
#
# The intention of this script is to assign each commit a unique version that
# is strictly increasing.
#
# We use the full git history to determine the current version, so ensure this
# is present when running this script. (When running in GitHub Actions, make
# sure the checkout action was configured with 'depth: 0').

# every version is prefixed by 0.0.
BASE_VERSION=0.0.

# the commit count up to the current commit (HEAD)
COMMIT_COUNT=$(git rev-list HEAD --count)

# append the branch name if we are not on the main branch
BRANCH=$(git rev-parse --abbrev-ref HEAD)

if [ $BRANCH != "main" ]; then
  POSTFIX=-$BRANCH
fi

# concat everything together
echo $BASE_VERSION$COMMIT_COUNT$POSTFIX

exit 0
