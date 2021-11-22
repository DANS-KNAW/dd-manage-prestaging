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
import net.sourceforge.argparse4j.inf.ArgumentAction;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import nl.knaw.dans.lib.dataverse.DataverseClient;
import nl.knaw.dans.prestaging.DdManagePrestagingConfiguration;
import nl.knaw.dans.prestaging.core.BasicFileMetaLoader;
import nl.knaw.dans.prestaging.core.GlobalIdIterator;
import nl.knaw.dans.prestaging.db.BasicFileMetaDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadFromDataverseCommand extends EnvironmentCommand<DdManagePrestagingConfiguration> {
    private static final Logger log = LoggerFactory.getLogger(LoadFromDataverseCommand.class);
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
        subparser.addArgument("--fail-on-error")
                .type(Boolean.class)
                .action(Arguments.storeTrue())
                .dest("failOnError")
                .help("Fail the run at the first error");
    }

    @Override
    protected void run(Environment environment, Namespace namespace, DdManagePrestagingConfiguration configuration) {
        log.trace("ENTER");
        BasicFileMetaDAO dao = new BasicFileMetaDAO(hibernate.getSessionFactory());
        DataverseClient client = configuration.getDataverse().build();
        // https://stackoverflow.com/questions/42384671/dropwizard-hibernate-no-session-currently-bound-to-execution-context
        BasicFileMetaLoader loaderProxy = new UnitOfWorkAwareProxyFactory(hibernate).create(
                BasicFileMetaLoader.class,
                new Class[]{BasicFileMetaDAO.class, DataverseClient.class, Boolean.class},
                new Object[]{dao, client, namespace.getBoolean("failOnError")});
        String doi = namespace.getString("doi");
        if (doi == null) {
            log.info("No DOI provided, loading all published datasets");
            loaderProxy.loadFromDatasets(new GlobalIdIterator(client, "publicationStatus:\"Published\""));
        } else {
            log.info("Loading from provided DOI {}", doi);
            loaderProxy.loadFromDataset(doi);
        }
    }
}
