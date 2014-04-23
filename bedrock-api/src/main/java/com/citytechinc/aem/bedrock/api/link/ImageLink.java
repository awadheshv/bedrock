/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.api.link;

/**
 * An image link contains all of the attributes of a <code>Link</code> with the addition of an image source attribute.
 */
public interface ImageLink extends Link {

    /**
     * @return image source or empty String if none
     */
    String getImageSource();
}
