/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node.impl;

import com.citytechinc.cq.library.constants.Constants;
import com.citytechinc.cq.library.content.link.Link;
import com.citytechinc.cq.library.content.link.builders.LinkBuilder;
import com.citytechinc.cq.library.content.node.BasicNode;
import com.citytechinc.cq.library.content.node.predicates.BasicNodeResourceTypePredicate;
import com.citytechinc.cq.library.content.page.PageDecorator;
import com.citytechinc.cq.library.content.page.PageManagerDecorator;
import com.day.cq.commons.DownloadResource;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.foundation.Image;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import java.util.Collections;
import java.util.List;

import static com.citytechinc.cq.library.content.link.impl.LinkFunctions.LINK_TO_HREF;
import static com.citytechinc.cq.library.content.link.impl.LinkFunctions.PATH_TO_LINK;
import static com.citytechinc.cq.library.content.node.impl.NodeFunctions.RESOURCE_TO_BASIC_NODE;
import static com.google.common.base.Preconditions.checkNotNull;

public final class DefaultBasicNode implements BasicNode {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBasicNode.class);

    private static final Predicate<BasicNode> ALL = Predicates.alwaysTrue();

    private final ValueMap properties;

    private final Resource resource;

    public DefaultBasicNode(final Resource resource) {
        checkNotNull(resource);

        this.resource = resource;

        properties = ResourceUtil.getValueMap(resource);
    }

    @Override
    public ValueMap asMap() {
        return properties;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final String propertyName) {
        final T value = (T) properties.get(checkNotNull(propertyName));

        return Optional.fromNullable(value);
    }

    @Override
    public <T> T get(final String propertyName, final T defaultValue) {
        return properties.get(checkNotNull(propertyName), defaultValue);
    }

    @Override
    public Optional<String> getAsHref(final String propertyName) {
        return getAsLink(propertyName).transform(LINK_TO_HREF);
    }

    @Override
    public String getAsHref(final String propertyName, final String defaultValue) {
        return getAsHref(propertyName).or(defaultValue);
    }

    @Override
    public Optional<String> getAsMappedHref(final String propertyName) {
        return getAsMappedLink(propertyName).transform(LINK_TO_HREF);
    }

    @Override
    public String getAsMappedHref(final String propertyName, final String defaultValue) {
        return getAsMappedHref(propertyName).or(defaultValue);
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName) {
        final Optional<String> pathOptional = get(propertyName);

        return pathOptional.transform(PATH_TO_LINK);
    }

    @Override
    public Optional<Link> getAsMappedLink(final String propertyName) {
        final Optional<String> pathOptional = get(propertyName);

        return pathOptional.transform(new Function<String, String>() {
            @Override
            public String apply(final String path) {
                return resource.getResourceResolver().map(path);
            }
        }).transform(PATH_TO_LINK);
    }

    @Override
    public Optional<PageDecorator> getAsPage(final String propertyName) {
        final Optional<PageDecorator> pageOptional;

        final String path = properties.get(checkNotNull(propertyName), "");

        if (path.isEmpty()) {
            pageOptional = Optional.absent();
        } else {
            final PageManagerDecorator pageManager = resource.getResourceResolver().adaptTo(PageManagerDecorator.class);

            pageOptional = pageManager.getPageOptional(path);
        }

        return pageOptional;
    }

    @Override
    public List<BasicNode> getNodes() {
        return getNodes(ALL);
    }

    @Override
    public List<BasicNode> getNodes(final Predicate<BasicNode> predicate) {
        checkNotNull(predicate);

        return FluentIterable.from(resource.getChildren()).transform(RESOURCE_TO_BASIC_NODE).filter(predicate).toList();
    }

    @Override
    public Optional<BasicNode> getNode(final String relativePath) {
        checkNotNull(relativePath);

        return Optional.fromNullable(resource.getChild(relativePath)).transform(RESOURCE_TO_BASIC_NODE);
    }

    @Override
    public List<BasicNode> getNodes(final String relativePath) {
        checkNotNull(relativePath);

        final Resource child = resource.getChild(relativePath);

        final List<BasicNode> nodes;

        if (child == null) {
            nodes = Collections.emptyList();
        } else {
            nodes = FluentIterable.from(child.getChildren()).transform(RESOURCE_TO_BASIC_NODE).toList();
        }

        return nodes;
    }

    @Override
    public List<BasicNode> getNodes(final String relativePath, final String resourceType) {
        return getNodes(relativePath, new BasicNodeResourceTypePredicate(resourceType));
    }

    @Override
    public List<BasicNode> getNodes(final String relativePath, final Predicate<BasicNode> predicate) {
        checkNotNull(relativePath);
        checkNotNull(predicate);

        final Resource child = resource.getChild(relativePath);

        final List<BasicNode> nodes;

        if (child == null) {
            nodes = Collections.emptyList();
        } else {
            nodes = FluentIterable.from(child.getChildren()).transform(RESOURCE_TO_BASIC_NODE).filter(predicate)
                .toList();
        }

        return nodes;
    }

    @Override
    public Optional<String> getImageReference() {
        return getImageReference(Constants.DEFAULT_IMAGE_NAME);
    }

    @Override
    public Optional<String> getImageReference(final String name) {
        checkNotNull(name);

        return Optional.fromNullable(properties.get(name + "/" + DownloadResource.PN_REFERENCE, String.class));
    }

    @Override
    public Optional<String> getImageRendition(final String renditionName) {
        checkNotNull(renditionName);

        return getImageRendition(Constants.DEFAULT_IMAGE_NAME, renditionName);
    }

    @Override
    public Optional<String> getImageRendition(final String name, final String renditionName) {
        checkNotNull(name);
        checkNotNull(renditionName);

        final Optional<String> pathOptional = getImageReference(name);

        Optional<String> imageRenditionOptional = Optional.absent();

        if (pathOptional.isPresent()) {
            // TODO replace with granite AssetManager?
            final Asset asset = resource.getResourceResolver().getResource(pathOptional.get()).adaptTo(Asset.class);

            if (asset != null) {
                final boolean valid = Iterables.any(asset.getRenditions(), new Predicate<Rendition>() {
                    @Override
                    public boolean apply(final Rendition rendition) {
                        return renditionName.equals(rendition.getName());
                    }
                });

                if (valid) {
                    imageRenditionOptional = Optional.of(asset.getRendition(renditionName).getPath());
                }
            }
        }

        return imageRenditionOptional;
    }

    @Override
    public Optional<Node> getNode() {
        return Optional.fromNullable(resource.adaptTo(Node.class));
    }

    @Override
    public int getIndex() {
        return getIndex(ALL);
    }

    @Override
    public int getIndex(final String resourceType) {
        return getIndex(new BasicNodeResourceTypePredicate(resourceType));
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public boolean isHasImage() {
        return isHasImage(Constants.DEFAULT_IMAGE_NAME);
    }

    @Override
    public boolean isHasImage(final String name) {
        checkNotNull(name);

        final Resource child = resource.getChild(name);

        return child != null && new Image(resource, name).hasContent();
    }

    @Override
    public String getPath() {
        return resource.getPath();
    }

    @Override
    public List<Property> getProperties(final Predicate<Property> predicate) {
        checkNotNull(predicate);

        final Node node = resource.adaptTo(Node.class);

        final List<Property> properties;

        if (node == null) {
            properties = Collections.emptyList();
        } else {
            properties = Lists.newArrayList();

            try {
                final PropertyIterator iterator = node.getProperties();

                while (iterator.hasNext()) {
                    final Property property = iterator.nextProperty();

                    if (predicate.apply(property)) {
                        properties.add(property);
                    }
                }
            } catch (RepositoryException e) {
                LOG.error("error getting properties for node = " + getPath(), e);
            }
        }

        return properties;
    }

    @Override
    public String getHref() {
        return getLink().getHref();
    }

    @Override
    public Link getLink() {
        return getLinkBuilder().build();
    }

    @Override
    public LinkBuilder getLinkBuilder() {
        return LinkBuilder.forResource(resource);
    }

    @Override
    public String getMappedHref() {
        return getMappedLink().getHref();
    }

    @Override
    public Link getMappedLink() {
        return getMappedLinkBuilder().build();
    }

    @Override
    public LinkBuilder getMappedLinkBuilder() {
        return LinkBuilder.forMappedResource(resource);
    }

    private int getIndex(final Predicate<BasicNode> predicate) {
        checkNotNull(predicate);

        int index = -1;

        final Resource parent = resource.getParent();

        if (parent != null) {
            final List<BasicNode> contentNodes = new DefaultBasicNode(parent).getNodes(predicate);

            int count = 0;

            for (final BasicNode node : contentNodes) {
                if (getPath().equals(node.getPath())) {
                    index = count;
                    break;
                }

                count++;
            }
        }

        return index;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("path", getPath()).add("properties", Maps.newHashMap(asMap()))
            .toString();
    }
}
