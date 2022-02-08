#!/bin/sh

if [ $# -lt 2 ]; then
  echo "Prints a progress report for a batch of deposits that is being ingested"
  echo "Usage: progress-report.sh <deposits-batch> <out-batch>"
  exit 1
fi

DEPOSITS=$1
OUT=$2

echo "results at $(date)

NUMBER OF DEPOSITS:
todo      : $(ls -1 $DEPOSITS | wc -l)
processed : $(ls -1 $OUT/processed/ | wc -l)
rejected  : $(ls -1 $OUT/rejected/  | wc -l)
failed    : $(ls -1 $OUT/failed/    | wc -l)

DISK USAGE:
todo      : $(du -sh $DEPOSITS)
processed : $(du -sh $OUT/processed/)
rejected  : $(du -sh $OUT/rejected/)
failed    : $(du -sh $OUT/failed/)

"
