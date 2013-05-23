/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.servlets.paragraphs;

public final class Paragraph {

    private final String path;

    private final String html;

    public Paragraph(final String path, final String html) {
        this.path = path;
        this.html = html;
    }

    public String getPath() {
        return path;
    }

    public String getHtml() {
        return html;
    }
}
