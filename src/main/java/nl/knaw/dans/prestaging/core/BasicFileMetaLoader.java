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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class BasicFileMetaLoader {
    private static final Logger log = LoggerFactory.getLogger(BasicFileMetaLoader.class);

    private final BasicFileMetaDAO dao;
    private final DataverseClient dataverseClient;

    public BasicFileMetaLoader(BasicFileMetaDAO dao, DataverseClient dataverseClient) {
        log.trace("ENTER");
        this.dao = dao;
        this.dataverseClient = dataverseClient;
    }

    public void loadFromDatasets(Iterator<String> dois) {
        log.trace("ENTER");
        Spliterator<String> spliterator = Spliterators.spliteratorUnknownSize(dois, Spliterator.ORDERED);
        StreamSupport.stream(spliterator, false).forEach(this::loadFromDataset);
    }

    public void loadFromDataset(String doi) {
        log.trace("ENTER");
        try {
            DataverseResponse<List<DatasetVersion>> r = dataverseClient.dataset(doi).getAllVersions();
            List<DatasetVersion> versions = r.getData();
            versions.sort(new DatasetVersionComparator());
            int seqNum = 1;
            for (DatasetVersion v : versions) {
                loadFromDatasetVersion(doi, v, seqNum);
                ++seqNum;
                log.info("Stored basic file metas for DOI: {}", doi);
            }
        } catch (IOException | DataverseException e) {
            log.error("Could not retrieve or store basic file metas for DOI: {}", doi, e);
        }
    }

    @UnitOfWork
    public void loadFromDatasetVersion(String doi, DatasetVersion v, int seqNum) {
        log.trace("ENTER");
        for (FileMeta f : v.getFiles()) {
            BasicFileMetaEntity basicFileMeta = new BasicFileMetaEntity();
            basicFileMeta.setDatasetDoi(doi);
            basicFileMeta.setStorageIdentifier(f.getDataFile().getStorageIdentifier());
            basicFileMeta.setVersionSequenceNumber(seqNum);
            basicFileMeta.setFileName(f.getLabel());
            basicFileMeta.setDirectoryLabel(f.getDirectoryLabel());
            basicFileMeta.setMimeType(f.getDataFile().getContentType());
            basicFileMeta.setSha1Checksum(f.getDataFile().getChecksum().getValue());
            dao.create(basicFileMeta);
        }
    }
}
