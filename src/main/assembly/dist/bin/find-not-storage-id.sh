#!/usr/bin/env bash

if [ $# -eq 0 ]; then
  echo "Finds all files that do not have a storage ID as a file name under a given base directory."
  echo "Usage: find-not-storage-id.sh <dir>"
  exit 1
fi

DIR=$1

find $DIR -type f -not -regex '^.*/[0-9a-f]+-[0-9a-f]+$'
