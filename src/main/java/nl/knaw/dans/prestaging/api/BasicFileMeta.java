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
package nl.knaw.dans.prestaging.api;

import nl.knaw.dans.lib.dataverse.model.file.prestaged.Checksum;
import nl.knaw.dans.lib.dataverse.model.file.prestaged.PrestagedFile;
import nl.knaw.dans.prestaging.core.BasicFileMetaEntity;

import java.util.Collections;

public class BasicFileMeta {
    private String label;
    private String directoryLabel;
    private int versionSequenceNumber;
    private PrestagedFile prestagedFile;

    public BasicFileMeta() {
    }

    public BasicFileMeta(BasicFileMetaEntity bfm) {
        label = bfm.getFileName();
        directoryLabel = bfm.getDirectoryLabel();
        versionSequenceNumber = bfm.getVersionSequenceNumber();
        prestagedFile = new PrestagedFile();
        prestagedFile.setFileName(bfm.getFileName());
        prestagedFile.setDirectoryLabel(bfm.getDirectoryLabel());
        prestagedFile.setChecksum(new Checksum(
                "SHA-1",
                bfm.getSha1Checksum()
        ));
        prestagedFile.setMimeType(bfm.getMimeType());
        prestagedFile.setStorageIdentifier(bfm.getStorageIdentifier());
        prestagedFile.setCategories(Collections.emptyList());
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDirectoryLabel() {
        return directoryLabel;
    }

    public void setDirectoryLabel(String directoryLabel) {
        this.directoryLabel = directoryLabel;
    }

    public int getVersionSequenceNumber() {
        return versionSequenceNumber;
    }

    public void setVersionSequenceNumber(int versionSequenceNumber) {
        this.versionSequenceNumber = versionSequenceNumber;
    }

    public PrestagedFile getPrestagedFile() {
        return prestagedFile;
    }

    public void setPrestagedFile(PrestagedFile prestagedFile) {
        this.prestagedFile = prestagedFile;
    }
}
