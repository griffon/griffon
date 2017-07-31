/*
 * Copyright 2008-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.util;

import griffon.core.resources.ResourceHandler;
import griffon.util.AbstractMapResourceBundle;
import griffon.util.ResourceBundleReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
@Named("xml")
public class XmlResourceBundleLoader extends AbstractResourceBundleLoader {
    protected static final String XML_SUFFIX = ".xml";

    protected final ResourceBundleReader resourceBundleReader;

    @Inject
    public XmlResourceBundleLoader(@Nonnull ResourceHandler resourceHandler,
                                   @Nonnull ResourceBundleReader resourceBundleReader) {
        super(resourceHandler);
        this.resourceBundleReader = requireNonNull(resourceBundleReader, "Argument 'resourceBundleReader' must not be null");
    }

    @Nonnull
    @Override
    public Collection<ResourceBundle> load(@Nonnull String name) {
        requireNonBlank(name, ERROR_FILENAME_BLANK);
        List<ResourceBundle> bundles = new ArrayList<>();
        List<URL> resources = getResources(name, XML_SUFFIX);

        if (resources != null) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = null;

            try {
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new IllegalStateException("Can not read " + name + XML_SUFFIX, e);
            }

            for (URL resource : resources) {
                if (null == resource) { continue; }
                try {
                    Document document = documentBuilder.parse(resource.openStream());
                    ResourceBundle bundle = resourceBundleReader.read(toResourceBundle(document));
                    bundles.add(bundle);
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        return bundles;
    }

    @Nonnull
    private ResourceBundle toResourceBundle(@Nonnull Document document) {
        document.getDocumentElement().normalize();

        final Map<String, Object> map = new LinkedHashMap<>();
        traverseNodes(map, document.getDocumentElement().getChildNodes());

        return new AbstractMapResourceBundle() {
            @Override
            protected void initialize(@Nonnull Map<String, Object> entries) {
                entries.putAll(map);
            }
        };
    }

    private void traverseNodes(@Nonnull Map<String, Object> accumulator, @Nonnull NodeList nodes) {
        for (int index = 0; index < nodes.getLength(); index++) {
            Node item = nodes.item(index);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            String key = item.getNodeName();
            if (item.hasChildNodes()) {
                if (item.getChildNodes().getLength() == 1) {
                    accumulator.put(key, item.getTextContent().trim());
                } else {
                    Map<String, Object> map = new LinkedHashMap<>();
                    traverseNodes(map, item.getChildNodes());
                    accumulator.put(key, map);
                }
            } else {
                accumulator.put(key, "");
            }
        }
    }
}
