#!/bin/sh

set -e

DATEPART=$(date +"%Y-%m-%d_%H-%M")
SCHEMA_FILE=dvndb-schema-$DATEPART.sql
DATA_FILE=dvndb-data-$DATEPART.sql
FULL_DUMP_FILE=dvndb-all-$DATEPART.sql
TEMPFOLDER=/tmp/dvndb-export-$DATEPART
ZIPFILE=dvndb-export-$DATEPART.zip
BACKUPS_DIR=/var/opt/dans.knaw.nl/tmp/migration/db-backups

PGPASSWORD=
IFS=
read -s  -p 'PostGreSQL password for dvnuser: ' PGPASSWORD

export PGPASSWORD

mkdir -p $TEMPFOLDER
echo -n "Create schema export..."
pg_dump --schema-only -U dvnuser dvndb > $TEMPFOLDER/$SCHEMA_FILE
echo "OK"
echo -n "Create data export..."
pg_dump --data-only -U dvnuser dvndb > $TEMPFOLDER/$DATA_FILE
echo "OK"
echo -n "Create full export..."
pg_dump -U dvnuser dvndb > $TEMPFOLDER/$FULL_DUMP_FILE
echo "OK"

echo -n "Creating ZIP file..."
zip -r $BACKUPS_DIR/$ZIPFILE $TEMPFOLDER
echo "OK"
echo "Created back-up in $BACKUPS_DIR/$ZIPFILE"
rm -fr $TEMPFOLDER
echo "Done"
