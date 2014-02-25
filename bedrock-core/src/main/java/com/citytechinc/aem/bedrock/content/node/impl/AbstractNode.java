/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.node.impl;

import com.citytechinc.aem.bedrock.content.link.Link;
import com.citytechinc.aem.bedrock.content.link.builders.LinkBuilder;
import com.citytechinc.aem.bedrock.content.page.PageDecorator;
import com.citytechinc.aem.bedrock.content.page.PageManagerDecorator;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import static com.citytechinc.aem.bedrock.content.link.impl.LinkFunctions.PATH_TO_LINK;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractNode {

    protected final Resource resource;

    protected AbstractNode(final Resource resource) {
        checkNotNull(resource);

        this.resource = resource;
    }

    protected Optional<Link> getLinkOptional(final Optional<String> pathOptional, final boolean strict,
        final boolean mapped) {
        final ResourceResolver resourceResolver = resource.getResourceResolver();

        final Function<String, Link> toLink;

        if (strict) {
            toLink = new Function<String, Link>() {
                @Override
                public Link apply(final String path) {
                    return LinkBuilder.forPath(resourceResolver, path).build();
                }
            };
        } else {
            toLink = PATH_TO_LINK;
        }

        return pathOptional.transform(new Function<String, String>() {
            @Override
            public String apply(final String path) {
                return mapped ? resourceResolver.map(path) : path;
            }
        }).transform(toLink);
    }

    protected Optional<PageDecorator> getPageOptional(final String path) {
        final Optional<PageDecorator> pageOptional;

        if (path.isEmpty()) {
            pageOptional = Optional.absent();
        } else {
            pageOptional = resource.getResourceResolver().adaptTo(PageManagerDecorator.class).getPageOptional(path);
        }

        return pageOptional;
    }
}
