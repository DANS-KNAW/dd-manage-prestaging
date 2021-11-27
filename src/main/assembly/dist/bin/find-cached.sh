#!/usr/bin/env bash

if [ $# -eq 0 ]; then
  echo "Finds the paths of cached metadata (.cached files) under a give base directory."
  echo "Usage: find-cached <dir>"
  exit 1
fi

DIR=$1

find $DIR -type f -name '*.cached'
