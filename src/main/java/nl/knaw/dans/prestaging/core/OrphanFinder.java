/*
 * Copyright (C) 2021 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.prestaging.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OrphanFinder {
    private static final Logger log = LoggerFactory.getLogger(OrphanFinder.class);
    private static final Pattern storedFilePattern = Pattern.compile("^\\d+-\\d+$");

    private final List<Path> storageDirs;
    private final CapturedStorageIdentifiers capturedStorageIdentifiers;
    private final OrphanRegister orphanRegister;

    public OrphanFinder(List<Path> storageDirs, CapturedStorageIdentifiers capturedStorageIdentifiers, OrphanRegister orphanRegister) {
        this.storageDirs = storageDirs;
        this.capturedStorageIdentifiers = capturedStorageIdentifiers;
        this.orphanRegister = orphanRegister;
    }

    public void searchStorageDirs() throws IOException {
        for (Path storageDir : storageDirs) {
            searchStorageDir(storageDir);
        }
    }

    public void searchStorageDir(Path dir) throws IOException {
        log.info("START: STORAGE DIR {}", dir);
        List<Path> files = Files.list(dir).collect(Collectors.toList());
        for (Path file : files) {
            searchDatasetStorageDir(file);
        }
        log.info("END: STORAGE DIR {}", dir);
    }

    void searchDatasetStorageDir(Path dir) throws IOException {
        log.info("Inspecting {}", dir);

        log.debug("Getting DOI from directory name");
        String doi = null;

        log.debug("Getting expected storage identifiers");
        Set<String> expected = new HashSet<>(capturedStorageIdentifiers.getForDoi(doi));

        log.debug("Getting storage identifiers found on disk");
        List<Path> found = getStoredFilesOnDisk(dir);

        log.debug("Removing expected from found");
        List<Path> unexpected = found.stream()
                .filter(f -> !expected.contains(f.getFileName().toString()))
                .collect(Collectors.toList());
        unexpected.forEach(orphanRegister::register);

        if (unexpected.isEmpty()) log.info("Directory OK");
        else log.error("Orphans found. NOT OK.");
    }

    private List<Path> getStoredFilesOnDisk(Path dir) throws IOException {
        return Files.list(dir)
                .filter(f -> storedFilePattern.matcher(f.getFileName().toString()).matches())
                .collect(Collectors.toList());
    }
}
