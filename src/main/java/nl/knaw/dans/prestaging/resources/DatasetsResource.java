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
package nl.knaw.dans.prestaging.resources;

import io.dropwizard.hibernate.UnitOfWork;
import nl.knaw.dans.prestaging.api.BasicFileMeta;
import nl.knaw.dans.prestaging.db.BasicFileMetaDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("/datasets")
@Produces(MediaType.APPLICATION_JSON)
public class DatasetsResource {
    private final BasicFileMetaDAO basicFileMetaDAO;

    public DatasetsResource(BasicFileMetaDAO basicFileMetaDAO) {
        this.basicFileMetaDAO = basicFileMetaDAO;
    }

    @GET
    @Path("/{id}/seq/{seqNr}/basic-file-metas")
    @UnitOfWork
    public List<BasicFileMeta> getBasicFileMetasForDoi(@PathParam("id") String id, @PathParam("seqNr") int seqNr, @QueryParam("persistentId") String persistentId) {
        if (":persistentId".equals(id)) {
            return basicFileMetaDAO.findByDoiAndSeqNr(persistentId, seqNr)
                    .stream().map(BasicFileMeta::new).collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Database IDs not supported");
        }
    }
}
