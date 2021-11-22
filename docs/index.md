dd-manage-prestaging
====================

SYNOPSIS
--------

    dd-manage-prestaging { server | check | load-from-dataverse [--include-easy-migration] | find-orphaned -o}


DESCRIPTION
-----------

Manage prestaging of Dataverse files for next migration round. The DANS migration strategy is to minimize the number of
times that data files have to be copied from EASY to the target data station. Once migrated a data file should be left
in the Dataverse storage to be imported as a pre-staged file the next iteration of the migration. This allows for an 
iterative approach when migrating the metadata. Each iteration of the migration can install Dataverse from scratch.

### Starting a new migration round.
Not all files in the storage directory can be reused in the next iteration. The following process should be followed to 
ensure that not storage is wasted on orphaned files:

1. After the migration round, build a new database of pre-staged file information:
   ```
   dd-manage-prestaging load-from-dataverse
   ```
2. Back up this database with the helper script `export-prestaging-info.sh`.
3. Find the files that are orphaned:
   ```
   dd-manage-prestaging find-orphaned -o orphans.txt
   ```
   Normally, this should only be the files from the `easy-migration` folder, which are excluded from the pre-staged files
   database, because they are bound to change from one iteration to the next.
4. Find the other files that Dataverse keeps, that cannot be reused:
   ```
   find <dataverse-files-dir> -type f | grep cached > cached.txt
   find <dataverse-files-dir> -type f | grep thumb > thumb.txt
   ```
5. Remove the Dataverse installation.
6. Delete the cached, thumbs files from storage.
7. The file count should now be exactly the same a the number of unique storage identifiers in the pre-staged file database.
   (Note that files can appear in multiple versions, so the storage identifiers are not unique by themselves!)
8. Install Dataverse.
9. Start an import with pre-staged files.

To do: describe how to deal with `.bak` and `.orig`.

ARGUMENTS
---------

        positional arguments:
        {server,check,load-from-dataverse,find-orphaned}   available commands
        
        named arguments:
        -h, --help             show this help message and exit
        -v, --version          show the application version and exit

INSTALLATION AND CONFIGURATION
------------------------------
Currently this project is built as an RPM package for RHEL7/CentOS7 and later. The RPM will install the binaries to
`/opt/dans.knaw.nl/dd-manage-prestaging` and the configuration files to `/etc/opt/dans.knaw.nl/dd-manage-prestaging`. 

For installation on systems that do no support RPM and/or systemd:

1. Build the tarball (see next section).
2. Extract it to some location on your system, for example `/opt/dans.knaw.nl/dd-manage-prestaging`.
3. Start the service with the following command
   ```
   /opt/dans.knaw.nl/dd-manage-prestaging/bin/dd-manage-prestaging server /opt/dans.knaw.nl/dd-manage-prestaging/cfg/config.yml 
   ```

BUILDING FROM SOURCE
--------------------
Prerequisites:

* Java 8 or higher
* Maven 3.3.3 or higher
* RPM

Steps:
    
    git clone https://github.com/DANS-KNAW/dd-manage-prestaging.git
    cd dd-manage-prestaging 
    mvn clean install

If the `rpm` executable is found at `/usr/local/bin/rpm`, the build profile that includes the RPM 
packaging will be activated. If `rpm` is available, but at a different path, then activate it by using
Maven's `-P` switch: `mvn -Pprm install`.

Alternatively, to build the tarball execute:

    mvn clean install assembly:single
