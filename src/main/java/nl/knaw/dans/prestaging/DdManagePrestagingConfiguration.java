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

package nl.knaw.dans.prestaging;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import nl.knaw.dans.lib.util.DataverseClientFactory;
import nl.knaw.dans.prestaging.core.Storage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class DdManagePrestagingConfiguration extends Configuration {

    // May be null, if not loading, but serving records to easy-convert-bag-to-deposit
    @Valid
    private DataverseClientFactory dataverse;

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    private Storage storage;


    public DataverseClientFactory getDataverse() {
        return dataverse;
    }

    public void setDataverse(DataverseClientFactory dataverse) {
        this.dataverse = dataverse;
    }


    public DataSourceFactory getDatabase() {
        return database;
    }


    public void setDatabase(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }


    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }
}
