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


import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrphanFinderTest {
    private static final Path testStorageDirs = Paths.get("src/test/resources/test-storage-dirs/");

    @Test
    public void findsOrphanInDoiDirectory() throws Exception {
        Path storageDir = testStorageDirs.resolve("10.1234/SHLDR");
        CapturedStorageIdentifiers csi = new CaptureStorageIdentifiersImpl(Arrays.asList("file://00000000000-000000000001", "file://00000000000-000000000002"));
        ListOrphanRegister orphanRegister = new ListOrphanRegister();
        StorageNamespace namespace = new StorageNamespace(storageDir, null);
        OrphanFinder finder = new OrphanFinder(Collections.singletonList(namespace), csi, orphanRegister);
        finder.searchStorageDirs();
        assertEquals(Collections.singletonList(
                        storageDir.resolve("my-suffix-1").resolve("00000000000-000000000003")),
                orphanRegister.getOrphans());
    }

    @Test
    public void canHandleEmptyDoiDirectory() throws Exception {
        Path storageDir = testStorageDirs.resolve("empty");
        CapturedStorageIdentifiers csi = new CaptureStorageIdentifiersImpl(Collections.emptyList());
        ListOrphanRegister orphanRegister = new ListOrphanRegister();
        StorageNamespace namespace = new StorageNamespace(storageDir, null);
        OrphanFinder finder = new OrphanFinder(Collections.singletonList(namespace), csi, orphanRegister);
        finder.searchStorageDirs();
        assertEquals(Collections.emptyList(), orphanRegister.getOrphans());

    }

    // TODO: use shoulder

    // TODO: ignores cached, .orig and thumb files

    // TODO: calculate DOI from path

}
