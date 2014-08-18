package com.citytechinc.aem.bedrock.core.tags

import com.google.common.xml.XmlEscapers
import groovy.util.logging.Slf4j

import javax.servlet.jsp.JspTagException

/**
 * Render a page or component property value.
 */
@Slf4j("LOG")
final class PropertyTag extends AbstractPropertyTag {

    private static final def ESCAPER = XmlEscapers.xmlContentEscaper()

    /**
     * Default value if property does not exist.
     */
    String defaultValue = ""

    /**
     * Should XML be escaped? Defaults to true.
     */
    String escapeXml

    @Override
    int doEndTag() throws JspTagException {
        def value

        if (inherit) {
            value = componentNode.getInherited(propertyName, defaultValue)
        } else {
            value = componentNode.get(propertyName, defaultValue)
        }

        boolean escapeXml = !this.escapeXml ? true : Boolean.valueOf(this.escapeXml)

        try {
            if (escapeXml) {
                pageContext.out.write(ESCAPER.escape(value))
            } else {
                pageContext.out.write(value)
            }
        } catch (IOException ioe) {
            LOG.error "error writing property value = $propertyName for name = ", ioe

            throw new JspTagException(ioe)
        }

        EVAL_PAGE
    }
}
