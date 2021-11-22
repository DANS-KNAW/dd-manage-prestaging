#!/bin/sh

TEMPOUT=prestaging.sql
OUTFILE=${1:-"prestaging.zip"}

pg_dump --data -U dd_manage_prestaging dd_manage_prestaging > $TEMPOUT
zip $OUTFILE $TEMPOUT
rm $TEMPOUT
