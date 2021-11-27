#!/usr/bin/env bash

if [ $# -eq 0 ]; then
  echo "Counts all regular files under a base directory."
  echo
  echo "Usage: count-files <dir>"
  exit 1
fi

DIR=$1

find $DIR -type f | wc -l
