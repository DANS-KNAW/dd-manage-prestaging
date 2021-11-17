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

import nl.knaw.dans.lib.dataverse.DataverseClient;
import nl.knaw.dans.lib.dataverse.DataverseException;
import nl.knaw.dans.lib.dataverse.DataverseResponse;
import nl.knaw.dans.lib.dataverse.SearchOptions;
import nl.knaw.dans.lib.dataverse.model.search.DatasetResultItem;
import nl.knaw.dans.lib.dataverse.model.search.SearchItemType;
import nl.knaw.dans.lib.dataverse.model.search.SearchResult;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collectors;

public class DoiIterator implements Iterator<String> {
    private final DataverseClient dataverseClient;
    private int start = 0;
    private Iterator<String> currentBatch = Collections.emptyIterator();

    public DoiIterator(DataverseClient dataverseClient) {
        this.dataverseClient = dataverseClient;
    }

    @Override
    public boolean hasNext() {
        if (!currentBatch.hasNext()) {
            currentBatch = getNextBatch(start);
            start += 10;
        }

        return currentBatch.hasNext();
    }

    @Override
    public String next() {
        return currentBatch.next();
    }

    private Iterator<String> getNextBatch(int start) {
        try {
            SearchOptions options = new SearchOptions();
            options.setStart(start);
            options.setTypes(Collections.singletonList(SearchItemType.dataset));
            DataverseResponse<SearchResult> r = dataverseClient.search().find("publicationStatus:\"Published\"", options);
            return r.getData()
                    .getItems()
                    .stream()
                    .map(i -> ((DatasetResultItem) i).getGlobalId())
                    .collect(Collectors.toList())
                    .iterator();
        } catch (IOException | DataverseException e) {
            throw new IllegalStateException("Cannot retrieve next batch of DOIs", e);
        }
    }
}
