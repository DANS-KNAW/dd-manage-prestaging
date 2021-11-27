#!/usr/bin/env bash

if [ $# -eq 0 ]; then
  echo "Finds the storage IDs from the file names on disk (assumes that find-not-storage-id.sh returns the empty set)"
  echo "Usage: find-storage-ids-on-disk.sh <dir>"
  exit 1
fi

DIR=$1

find $DIR -type f | sed -r 's|^.*/(.*)$|file://\1|'

