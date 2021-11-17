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
import nl.knaw.dans.lib.dataverse.DataverseClient;
import nl.knaw.dans.lib.dataverse.DataverseException;
import nl.knaw.dans.lib.dataverse.DataverseResponse;
import nl.knaw.dans.lib.dataverse.model.dataset.DatasetVersion;
import nl.knaw.dans.lib.dataverse.model.file.FileMeta;
import nl.knaw.dans.prestaging.db.BasicFileMetaDAO;

import java.io.IOException;
import java.util.List;

public class Worker {
    private final BasicFileMetaDAO dao;
    private DataverseClient dataverseClient;

    public Worker(BasicFileMetaDAO dao, DataverseClient dataverseClient) {
        this.dao = dao;
        this.dataverseClient = dataverseClient;
    }

    @UnitOfWork
    public void loadBasicFileMetasFor(String doi) {
        try {
            DataverseResponse<List<DatasetVersion>> r = dataverseClient.dataset(doi).getAllVersions();
            r.getData().forEach(v -> {

            });


            BasicFileMeta basicFileMeta = new BasicFileMeta();
            basicFileMeta.setDatasetDoi("doi:test");
            basicFileMeta.setStorageIdentifier("storageIdtest");
            basicFileMeta.setVersionSequenceNumber(1);
            basicFileMeta.setFileName("Filename test");
            basicFileMeta.setMimeType("mime/type");
            basicFileMeta.setSha1Checksum("abc123");

            dao.create(basicFileMeta);
        } catch (IOException | DataverseException e) {
            e.printStackTrace();
        }
    }

    public void loadBasicFileMetadasFrom(DatasetVersion v) {
        for (FileMeta f: v.getFiles()) {

        }

    }

}
