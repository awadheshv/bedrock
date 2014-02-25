/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.testing.specs

import com.citytechinc.aem.spock.specs.AbstractSlingRepositorySpec
import com.citytechinc.cq.groovy.extension.builders.NodeBuilder
import com.citytechinc.cq.groovy.extension.builders.PageBuilder
import com.citytechinc.cq.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.aem.bedrock.content.node.ComponentNode
import com.citytechinc.aem.bedrock.content.node.impl.DefaultComponentNode
import com.citytechinc.aem.bedrock.content.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.content.page.impl.DefaultPageManagerDecorator
import com.day.cq.wcm.api.components.ComponentManager
import com.day.cq.wcm.api.designer.Designer
import com.day.cq.wcm.core.impl.components.ComponentCacheImpl
import com.day.cq.wcm.core.impl.components.ComponentManagerImpl
import com.day.cq.wcm.core.impl.designer.DesignCacheImpl
import com.day.cq.wcm.core.impl.designer.DesignerImpl
import spock.lang.Shared

/**
 * Spock specification for CQ testing with builders for creating test content.
 */
abstract class AbstractCqSpec extends AbstractSlingRepositorySpec {

    @Shared nodeBuilder

    @Shared pageBuilder

    @Override
    void addResourceAdapters() {
        addResourceAdapter(ComponentNode, { resource ->
            new DefaultComponentNode(resource)
        })
    }

    @Override
    void addResourceResolverAdapters() {
        addResourceResolverAdapter(PageManagerDecorator, {
            resourceResolver -> new DefaultPageManagerDecorator(resourceResolver)
        })

        def componentCache = new ComponentCacheImpl()

        componentCache.session = session

        addResourceResolverAdapter(ComponentManager, {
            resourceResolver -> new ComponentManagerImpl(resourceResolver, componentCache)
        })

        def designCache = new DesignCacheImpl()

        designCache.session = session

        addResourceResolverAdapter(Designer, {
            resourceResolver -> new DesignerImpl(resourceResolver, designCache)
        })
    }

    def getComponentNode(path) {
        def resource = resourceResolver.getResource(path)

        new DefaultComponentNode(resource)
    }

    def getPage(path) {
        resourceResolver.adaptTo(PageManagerDecorator).getPage(path)
    }

    def setupSpec() {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()

        nodeBuilder = new NodeBuilder(session)
        pageBuilder = new PageBuilder(session)
    }
}
