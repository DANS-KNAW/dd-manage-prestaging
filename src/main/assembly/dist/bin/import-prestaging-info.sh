#!/bin/sh

TEMPIN=prestaging.sql
INFILE=${1:-"prestaging.zip"}

unzip $INFILE

psql -U dd_manage_prestaging dd_manage_prestaging < $TEMPIN
rm $TEMPIN
