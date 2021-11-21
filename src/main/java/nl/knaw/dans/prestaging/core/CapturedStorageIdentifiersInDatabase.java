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
import nl.knaw.dans.prestaging.db.BasicFileMetaDAO;

import java.util.List;
import java.util.stream.Collectors;

public class CapturedStorageIdentifiersInDatabase implements CapturedStorageIdentifiers {
    private final BasicFileMetaDAO basicFileMetaDAO;

    CapturedStorageIdentifiersInDatabase(BasicFileMetaDAO basicFileMetaDAO) {
        this.basicFileMetaDAO = basicFileMetaDAO;
    }

    @Override
    @UnitOfWork
    // TODO: remove doubles? (a storage identifier can appear in several versions)
    public List<String> getForDoi(String doi) {
        return basicFileMetaDAO.findByDoi(doi).stream()
                .map(BasicFileMetaEntity::getStorageIdentifier)
                .collect(Collectors.toList());
    }
}
