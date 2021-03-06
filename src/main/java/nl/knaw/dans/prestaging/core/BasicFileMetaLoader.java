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

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.util.List;

public class BasicFileMetaLoader {
    private static final Logger log = LoggerFactory.getLogger(BasicFileMetaLoader.class);

    private final BasicFileMetaDAO dao;
    private final DataverseClient dataverseClient;
    private final boolean failOnError;
    private final boolean excludeEasyMigration;

    public BasicFileMetaLoader(BasicFileMetaDAO dao, DataverseClient dataverseClient, Boolean failOnError, Boolean excludeEasyMigration) {
        log.trace("ENTER");
        this.dao = dao;
        this.dataverseClient = dataverseClient;
        this.failOnError = failOnError;
        this.excludeEasyMigration = excludeEasyMigration;
    }

    public void loadFromDataset(String doi) {
        log.trace("ENTER");
        try {
            log.debug("Getting versions for DOI {}", doi);
            DataverseResponse<List<DatasetVersion>> r = dataverseClient.dataset(doi).getAllVersions();
            List<DatasetVersion> versions = r.getData();
            versions.sort(new DatasetVersionComparator());
            int seqNum = 1;
            for (DatasetVersion v : versions) {
                try {
                    int count = loadFromDatasetVersion(doi, v, seqNum);
                    log.info("Stored {} basic file metas for DOI {}, Version seqNr {}", count, doi, seqNum);
                    ++seqNum;
                } catch (PersistenceException e) {
                    // Catch outside UnitOfWork, as exceptions will not occur until commit.
                    log.error("Error saving basic file metas for DOI {}, seqNr {}: {}", doi, seqNum, e.getMessage());
                    if (failOnError) throw new RuntimeException(e);
                }
            }
        } catch (IOException | DataverseException e) {
            log.error("Could not retrieve or store basic file metas for DOI: {}", doi, e);
            if (failOnError) throw new RuntimeException(e);
        }
    }

    @UnitOfWork
    public int loadFromDatasetVersion(String doi, DatasetVersion v, int seqNum) {
        log.trace("ENTER");
        int count = 0;
        for (FileMeta f : v.getFiles()) {
            if (excludeEasyMigration && "easy-migration".equals(f.getDirectoryLabel())) {
                log.debug("Skipping easy-migration file");
                continue;
            }
            BasicFileMetaEntity basicFileMeta = new BasicFileMetaEntity();
            basicFileMeta.setDatasetDoi(doi);
            basicFileMeta.setStorageIdentifier(f.getDataFile().getStorageIdentifier());
            basicFileMeta.setVersionSequenceNumber(seqNum);
            basicFileMeta.setFileName(f.getLabel());
            basicFileMeta.setDirectoryLabel(f.getDirectoryLabel());
            basicFileMeta.setMimeType(f.getDataFile().getContentType());
            basicFileMeta.setSha1Checksum(f.getDataFile().getChecksum().getValue());
            dao.create(basicFileMeta);
            ++count;
            log.debug("Stored file, label: {}, directoryLabel: {}", basicFileMeta.getFileName(), basicFileMeta.getDirectoryLabel());
        }
        return count;
    }
}
