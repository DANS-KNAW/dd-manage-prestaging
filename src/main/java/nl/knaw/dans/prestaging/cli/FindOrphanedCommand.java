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
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import nl.knaw.dans.lib.util.DefaultConfigEnvironmentCommand;
import nl.knaw.dans.prestaging.DdManagePrestagingConfiguration;
import nl.knaw.dans.prestaging.core.*;
import nl.knaw.dans.prestaging.db.BasicFileMetaDAO;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FindOrphanedCommand extends DefaultConfigEnvironmentCommand<DdManagePrestagingConfiguration> {
    private static final Logger log = LoggerFactory.getLogger(FindOrphanedCommand.class);
    private static final String outputFile = "outputFile";
    private final HibernateBundle<DdManagePrestagingConfiguration> hibernate;

    public FindOrphanedCommand(Application<DdManagePrestagingConfiguration> application, HibernateBundle<DdManagePrestagingConfiguration> hibernate) {
        super(application, "find-orphaned", "Finds files on disk storage that have no basic file metadata in the manage-prestaging database");
        this.hibernate = hibernate;
    }

    @Override
    public void configure(Subparser subparser) {
        super.configure(subparser);
        subparser.addArgument("-o", "--output-file")
                .required(true)
                .dest(outputFile)
                .help("The file to write the orphan paths to");
        subparser.addArgument("--exclude-easy-migration")
                .type(Boolean.class)
                .action(Arguments.storeTrue())
                .dest("excludeEasyMigration")
                .help("Exclude easy-migration metadata files");
    }

    @Override
    protected void run(Environment environment, Namespace namespace, DdManagePrestagingConfiguration configuration) throws Exception {
        log.trace("ENTER");
        BasicFileMetaDAO dao = new BasicFileMetaDAO(hibernate.getSessionFactory());
        Path outPath = Paths.get(namespace.getString(outputFile));
        try (PrintWriter w = new PrintWriter(new FileWriterWithEncoding(outPath.toFile(), StandardCharsets.UTF_8))) {
            OrphanRegister register = new WriterOrphanRegister(w);
            OrphanFinder finderProxy = new UnitOfWorkAwareProxyFactory(hibernate).create(
                    OrphanFinder.class,
                    new Class[]{List.class, CapturedStorageIdentifiers.class, OrphanRegister.class},
                    new Object[]{configuration.getStorage().getNamespaces(), new CapturedStorageIdentifiersInDatabase(dao, namespace.getBoolean("excludeEasyMigration")), register});
            finderProxy.searchStorageDirs();
        }
    }
}
