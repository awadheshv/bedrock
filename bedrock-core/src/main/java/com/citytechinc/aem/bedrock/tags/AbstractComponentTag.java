/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.tags;

import com.citytechinc.aem.bedrock.content.node.ComponentNode;

import javax.servlet.jsp.tagext.TagSupport;

import static com.citytechinc.aem.bedrock.bindings.ComponentBindings.COMPONENT_NODE;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Base class for tag handlers that access a <code>ComponentNode</code> from the current context.
 */
public abstract class AbstractComponentTag extends TagSupport {

    private static final long serialVersionUID = 1L;

    /**
     * Should property value be inherited? Defaults to false.
     */
    private String inherit;

    public final String getInherit() {
        return inherit;
    }

    protected final ComponentNode getComponentNode() {
        return (ComponentNode) pageContext.getAttribute(COMPONENT_NODE);
    }

    protected final boolean isInherit() {
        return isNullOrEmpty(inherit) ? false : Boolean.valueOf(inherit);
    }

    public final void setInherit(final String inherit) {
        this.inherit = inherit;
    }
}
