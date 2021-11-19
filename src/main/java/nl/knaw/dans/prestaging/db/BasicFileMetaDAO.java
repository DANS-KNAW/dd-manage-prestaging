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
package nl.knaw.dans.prestaging.db;

import io.dropwizard.hibernate.AbstractDAO;
import nl.knaw.dans.prestaging.core.BasicFileMeta;
import org.hibernate.SessionFactory;

import java.util.List;

public class BasicFileMetaDAO extends AbstractDAO<BasicFileMeta> {

    public BasicFileMetaDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Creates a new {@link BasicFileMeta} record. If one with the same key already exists an exception is thrown.
     *
     * @param basicFileMeta the object to create
     * @return the object that was created
     */
    public BasicFileMeta create(BasicFileMeta basicFileMeta) {
        currentSession().save(basicFileMeta);
        return basicFileMeta;
    }

    public List<BasicFileMeta> findByDoiAndSeqNr(String doi, int seqNr) {
        return namedTypedQuery("BasicFileMetaKey.findByDoiAndSeqNr")
                .setParameter("doi", doi)
                .setParameter("seqNr", seqNr)
                .getResultList();
    }

}
