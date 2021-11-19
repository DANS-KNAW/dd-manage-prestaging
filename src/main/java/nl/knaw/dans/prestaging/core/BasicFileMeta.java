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

import javax.persistence.*;

@Entity
@IdClass(BasicFileMetaKey.class)
@Table(name = "basic_file_meta")
@NamedQueries({
        @NamedQuery(name = "BasicFileMetaKey.findByDoiAndSeqNr",
                query = "SELECT b FROM BasicFileMeta b WHERE b.datasetDoi=:doi AND b.versionSequenceNumber=:seqNr"),

})
public class BasicFileMeta {

    @Id
    @Column(name = "storage_identifier", nullable = false, length = 60)
    private String storageIdentifier;

    @Id
    @Column(name = "dataset_doi", nullable = false, length = 100)
    private String datasetDoi;

    @Id
    @Column(name = "version_sequence_number", nullable = false)
    private int versionSequenceNumber;

    @Column(name = "file_name", nullable = false, length = 1000)
    private String fileName;

    @Column(name = "directory_label", nullable = true, length = 1000)
    private String directoryLabel;

    @Column(name = "mime_type", nullable = false, length = 255)
    private String mimeType;

    @Column(name = "sha1_checksum", nullable = false, length = 40)
    private String sha1Checksum;

    public String getStorageIdentifier() {
        return storageIdentifier;
    }

    public void setStorageIdentifier(String storageIdentifier) {
        this.storageIdentifier = storageIdentifier;
    }

    public String getDatasetDoi() {
        return datasetDoi;
    }

    public void setDatasetDoi(String datasetDoi) {
        this.datasetDoi = datasetDoi;
    }

    public int getVersionSequenceNumber() {
        return versionSequenceNumber;
    }

    public void setVersionSequenceNumber(int versionSequenceNumber) {
        this.versionSequenceNumber = versionSequenceNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDirectoryLabel() {
        return directoryLabel;
    }

    public void setDirectoryLabel(String directoryLabel) {
        this.directoryLabel = directoryLabel;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSha1Checksum() {
        return sha1Checksum;
    }

    public void setSha1Checksum(String sha1Checksum) {
        this.sha1Checksum = sha1Checksum;
    }
}
