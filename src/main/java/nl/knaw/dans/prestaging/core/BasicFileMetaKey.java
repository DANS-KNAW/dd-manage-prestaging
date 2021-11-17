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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

// Multi-column primary key, see: https://stackoverflow.com/a/41144119
public class BasicFileMetaKey implements Serializable {
    private String storageIdentifier;
    private String datasetDoi;
    private int versionSequenceNumber;

    // Equals contract implementation generated with IntelliJ
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicFileMetaKey)) return false;
        BasicFileMetaKey that = (BasicFileMetaKey) o;
        return new EqualsBuilder()
                .append(versionSequenceNumber, that.versionSequenceNumber)
                .append(storageIdentifier, that.storageIdentifier)
                .append(datasetDoi, that.datasetDoi).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(storageIdentifier)
                .append(datasetDoi)
                .append(versionSequenceNumber).toHashCode();
    }
}
