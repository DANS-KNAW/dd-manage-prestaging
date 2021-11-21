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
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import nl.knaw.dans.prestaging.DdManagePrestagingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindOrphanedCommand extends EnvironmentCommand<DdManagePrestagingConfiguration> {
    private static final Logger log = LoggerFactory.getLogger(FindOrphanedCommand.class);
    private final HibernateBundle<DdManagePrestagingConfiguration> hibernate;

    public FindOrphanedCommand(Application<DdManagePrestagingConfiguration> application, HibernateBundle<DdManagePrestagingConfiguration> hibernate) {
        super(application, "find-orphaned", "Finds files on disk storage that have no basic file metadata in the manage-prestaging database");
        this.hibernate = hibernate;
    }


    @Override
    protected void run(Environment environment, Namespace namespace, DdManagePrestagingConfiguration configuration) throws Exception {

    }
}
