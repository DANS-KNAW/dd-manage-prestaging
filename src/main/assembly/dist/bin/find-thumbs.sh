#!/usr/bin/env bash

if [ $# -eq 0 ]; then
  echo "Finds the thumbnail files under a given base directory."
  echo "Usage: find-thumbs <dir>"
  exit 1
fi

DIR=$1

find $DIR -type f -name '*.thumb*'

