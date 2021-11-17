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
package nl.knaw.dans.prestaging.cli;

import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import nl.knaw.dans.lib.dataverse.DataverseClient;
import nl.knaw.dans.prestaging.DdManagePrestagingConfiguration;
import nl.knaw.dans.prestaging.core.BasicFileMetaLoader;
import nl.knaw.dans.prestaging.db.BasicFileMetaDAO;

public class LoadFromDataverseCommand extends EnvironmentCommand<DdManagePrestagingConfiguration> {
    private final HibernateBundle<DdManagePrestagingConfiguration> hibernate;

    public LoadFromDataverseCommand(Application<DdManagePrestagingConfiguration> application, HibernateBundle<DdManagePrestagingConfiguration> hibernate) {
        super(application, "load-from-dataverse", "Loads basic file metas from the configured Dataverse instance");
        this.hibernate = hibernate;
    }

    @Override
    public void configure(Subparser subparser) {
        super.configure(subparser);
        subparser.addArgument("--doi")
                .dest("doi")
                .help("The DOI for which to load the basic file metas");
        subparser.addArgument("--all")
                .action(Arguments.storeTrue())
                .dest("loadAll")
                .help("Load basic file metas for all published datasets in target Dataverse");
    }

    @Override
    protected void run(Environment environment, Namespace namespace, DdManagePrestagingConfiguration configuration) {
        BasicFileMetaDAO dao = new BasicFileMetaDAO(hibernate.getSessionFactory());
        DataverseClient client = configuration.getDataverse().build();
        // https://stackoverflow.com/questions/42384671/dropwizard-hibernate-no-session-currently-bound-to-execution-context
        BasicFileMetaLoader proxy = new UnitOfWorkAwareProxyFactory(hibernate).create(
                BasicFileMetaLoader.class,
                new Class[]{BasicFileMetaDAO.class, DataverseClient.class},
                new Object[]{dao, client});
        proxy.loadFromDataset(namespace.getString("doi"));
    }
}
