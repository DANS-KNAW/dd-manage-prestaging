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

import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OrphanFinder {
    private static final Logger log = LoggerFactory.getLogger(OrphanFinder.class);
    private static final Pattern storedFilePattern = Pattern.compile("^\\p{XDigit}+-\\p{XDigit}+$");

    private final List<StorageNamespace> storageNamespaces;
    private final CapturedStorageIdentifiers capturedStorageIdentifiers;
    private final OrphanRegister orphanRegister;

    public OrphanFinder(List<StorageNamespace> storageNamespaces, CapturedStorageIdentifiers capturedStorageIdentifiers, OrphanRegister orphanRegister) {
        this.storageNamespaces = storageNamespaces;
        this.capturedStorageIdentifiers = capturedStorageIdentifiers;
        this.orphanRegister = orphanRegister;
    }

    public void searchStorageDirs() throws IOException {
        for (StorageNamespace storageNamespace : storageNamespaces) {
            searchStorageNamespace(storageNamespace);
        }
    }

    public void searchStorageNamespace(StorageNamespace namespace) throws IOException {
        log.info("START: STORAGE DIR {}", namespace);
        Path nameSpaceDir = namespace.getDir().resolve(Optional.ofNullable(namespace.getShoulder()).orElse(""));
        if (Files.isDirectory(nameSpaceDir)) {
            List<Path> subDirs = Files.list(nameSpaceDir).filter(Files::isDirectory).collect(Collectors.toList());
            for (Path subDir : subDirs) {
                searchDatasetStorageDir(subDir, getDoi(namespace, subDir));
            }
        } else {
            log.warn("Directory {} does not exist (or is a regular file). Ignoring...", nameSpaceDir);
        }
        log.info("END: STORAGE DIR {}", namespace);
    }

    @UnitOfWork
    void searchDatasetStorageDir(Path doiDir, String doi) throws IOException {
        log.info("Inspecting {}", doiDir);

        List<String> expected = capturedStorageIdentifiers.getForDoi(doi);
        log.debug("Expected = {}", expected);

        List<Path> found = getStoredFilesOnDisk(doiDir);
        log.debug("Found = {}", found);

        log.debug("Removing expected from found");
        List<Path> unexpected = found.stream()
                .filter(f -> !expected.contains("file://" + f.getFileName().toString()))
                .collect(Collectors.toList());
        for (Path path : unexpected) {
            orphanRegister.register(path);
        }

        if (unexpected.isEmpty()) log.info("Directory OK");
        else log.error("Orphans found. NOT OK.");
    }

    private String getDoi(StorageNamespace namespace, Path doiDir) {
        return "doi:"
                + namespace.getDir().getFileName() + "/"
                + Optional.ofNullable(namespace.getShoulder()).map(s -> s + "/").orElse("")
                + doiDir.getFileName();
    }

    private List<Path> getStoredFilesOnDisk(Path dir) throws IOException {
        return Files.list(dir)
                .filter(f -> storedFilePattern.matcher(f.getFileName().toString()).matches())
                .collect(Collectors.toList());
    }
}
