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

import nl.knaw.dans.lib.dataverse.model.file.prestaged.PrestagedFile;

import javax.persistence.*;

@Entity
@Table(name = "basic_file_meta")
public class BasicFileMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String label;
    private String directoryLabel;
    private int versionSequenceNumber;
//    private PrestagedFile prestagedFile;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

//    public PrestagedFile getPrestagedFile() {
//        return prestagedFile;
//    }
//
//    public void setPrestagedFile(PrestagedFile prestagedFile) {
//        this.prestagedFile = prestagedFile;
//    }
}
