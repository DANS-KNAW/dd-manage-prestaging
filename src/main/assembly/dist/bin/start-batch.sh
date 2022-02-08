#!/bin/sh

if [ $# -lt 1 ]; then
  echo "Starts a batch"
  echo "Usage: start-batch.sh <deposits-batch>"
  exit 1
fi

BATCHDIR=$1
DATE_BATCH=${BATCHDIR#*/}
OUTDIR=out/$DATE_BATCH

export LOGDIR=logs/$DATE_BATCH
echo "Creating directory for logs at $LOGDIR"
mkdir -p $LOGDIR

BINPATH=$(command readlink -f $0 2> /dev/null || command grealpath $0 2> /dev/null)
export LOGBACK_CFG=$(dirname $BINPATH)/logback.xml
echo "Using logback configuration at $LOGBACK_CFG"

echo "Starting batch $DATE_BATCH at $(date)"
time dd-dans-deposit-to-dataverse import $BATCHDIR $OUTDIR
