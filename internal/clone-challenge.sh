#!/bin/bash
# This script copies the current working copy
# EXCLUDING the directory 'internal' to a temp dir
# then creates an empty git repo and commits everything as the
# 'Initial commit'
# It then pushes to 3 remotes
# git@github.com:sumup/coding-challenge-android-YYYY-MM-dd_COUNTER.git
# e.g.
# git@github.com:sumup/coding-challenge-android-2018-04-20_1.git
#
# Make sure to create these remotes before. In that sense, slack command can help:
#	 e.g. /create-repo coding-challenge-android-2018-04-20_1 chapter-mobile-coding-challenge

set -e
set -u

cd "$(dirname "$0")/.."

if [[ ! -d '.git' ]];then
  echo "no .git here, aborting"
  exit 42
fi

CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
if [ "${CURRENT_BRANCH}" != "master" ];then
  echo "you must check out master first"
  exit 42
fi

INTERNAL_DIR=$(basename "$(dirname "$0")")
TEMP_DIR=$(mktemp -d /tmp/temp-dir-clone-XXX)

echo "rsync to ${TEMP_DIR}"
rsync -az --exclude="${INTERNAL_DIR}" --exclude='.git' . "${TEMP_DIR}/"

echo 'initializing git repo'
cd "${TEMP_DIR}"
git init
git add .
git commit -m "Initial commit"
git log

PREFIX=$(date "+%Y-%m-%d")

for COUNTER in $(seq 1 3);do
  REMOTE=remote-$COUNTER
  echo "pushing to $REMOTE"
  git remote add "$REMOTE" "git@github.com:sumup/coding-challenge-android-${PREFIX}_${COUNTER}.git"
  git push -f -v --set-upstream "$REMOTE" master
done

echo "deleting ${TEMP_DIR}/"
rm -rf "$TEMP_DIR"
