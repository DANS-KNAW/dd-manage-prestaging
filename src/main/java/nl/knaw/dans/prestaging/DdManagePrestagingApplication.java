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

import com.fasterxml.jackson.annotation.JsonInclude;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.knaw.dans.prestaging.cli.LoadFromDataverseCommand;
import nl.knaw.dans.prestaging.core.BasicFileMetaEntity;
import nl.knaw.dans.prestaging.db.BasicFileMetaDAO;
import nl.knaw.dans.prestaging.resources.DatasetsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DdManagePrestagingApplication extends Application<DdManagePrestagingConfiguration> {
    private static final Logger log = LoggerFactory.getLogger(DdManagePrestagingApplication.class);

    private final HibernateBundle<DdManagePrestagingConfiguration> hibernate = new HibernateBundle<DdManagePrestagingConfiguration>(BasicFileMetaEntity.class) {

        @Override
        public DataSourceFactory getDataSourceFactory(DdManagePrestagingConfiguration configuration) {
            return configuration.getDatabase();
        }
    };

    public static void main(final String[] args) throws Exception {
        new DdManagePrestagingApplication().run(args);
    }

    @Override
    public String getName() {
        return "Manage Prestaging";
    }

    @Override
    public void initialize(final Bootstrap<DdManagePrestagingConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
        bootstrap.addCommand(new LoadFromDataverseCommand(this, hibernate));
        bootstrap.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor(false)));
    }

    @Override
    public void run(final DdManagePrestagingConfiguration configuration, final Environment environment) {
        log.trace("ENTER");
        BasicFileMetaDAO dao = new BasicFileMetaDAO(hibernate.getSessionFactory());
        environment.jersey().register(new DatasetsResource(dao));
    }
}
