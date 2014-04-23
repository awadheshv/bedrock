/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.core.tags;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.WCMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_PAGE_NAME;

/**
 * Render the keywords for the current page.
 */
public final class KeywordsTag extends AbstractMetaTag {

    private static final Logger LOG = LoggerFactory.getLogger(KeywordsTag.class);

    private static final long serialVersionUID = 1L;

    private static final String TAG_START = "<meta name=\"keywords\" content=\"";

    @Override
    public int doEndTag() throws JspTagException {
        final Page currentPage = (Page) pageContext.getAttribute(DEFAULT_CURRENT_PAGE_NAME);

        final StringBuilder builder = new StringBuilder();

        builder.append(TAG_START);
        builder.append(getXssApi().encodeForHTMLAttr(WCMUtils.getKeywords(currentPage, false)));
        builder.append(getTagEnd());

        try {
            pageContext.getOut().write(builder.toString());
        } catch (IOException ioe) {
            LOG.error("error writing keywords tag for page = " + currentPage.getPath(), ioe);

            throw new JspTagException(ioe);
        }

        return EVAL_PAGE;
    }
}
