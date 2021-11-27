#!/usr/bin/env bash

if [ $# -eq 0 ]; then
  echo "Counts the grand total of lines in the specified input files."
  echo "Usage: count-lines <file1> <file2> ..."
  exit 1
fi

EXPR="$(cat $1 | wc -l)"
shift
for FILE in "$@"
do
  EXPR="$EXPR + $(cat $FILE | wc -l)"
done

echo $(($EXPR))
