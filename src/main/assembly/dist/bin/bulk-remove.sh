#!/usr/bin/env bash

if [ $# -eq 0 ]; then
  echo "Removes the files specified in the input file. The input file must be a newline separated list of absoluted paths."
  echo "Usage: bulk-remove <input-file>"
  exit 1
fi

INPUT_FILE=$1

cat $INPUT_FILE | xargs /bin/rm
