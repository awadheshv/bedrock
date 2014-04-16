/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.node;

import com.citytechinc.aem.bedrock.content.link.ImageSource;
import com.citytechinc.aem.bedrock.content.link.Link;
import com.citytechinc.aem.bedrock.content.link.Linkable;
import com.citytechinc.aem.bedrock.content.page.PageDecorator;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.Node;
import javax.jcr.Property;
import java.util.List;

/**
 * Represents a "basic" node in the JCR, typically an unstructured node that may or may not exist in a CQ page
 * hierarchy.  Examples of non-page descendant nodes that could be considered basic nodes include design nodes and
 * arbitrary unstructured nodes that do not require inheritance capabilities.
 * <p/>
 * Many methods return an <a href="https://code.google.com/p/guava-libraries/wiki/UsingAndAvoidingNullExplained#Optional">Optional</a>
 * type where a null instance would otherwise be returned (e.g. when a descendant node is requested for a path that does
 * not exist in the repository).
 */
public interface BasicNode extends Linkable, ImageSource {

    /**
     * @return map of property names to values, or empty map if underlying resource is null or nonexistent
     */
    ValueMap asMap();

    /**
     * Get a property value from the current node, returning the default value if the property does not exist.
     *
     * @param <T> property type
     * @param propertyName property name
     * @param defaultValue default value
     * @return property value or default value if it does not exist
     */
    <T> T get(String propertyName, T defaultValue);

    /**
     * Get a property value from the current node.  This returns the same value as the underlying <code>ValueMap</code>
     * wrapped in an <code>Optional</code> instance instead of returning null.
     *
     * @param propertyName property name
     * @param type property type
     * @param <T> property type
     * @return <code>Optional</code> of the given type containing the property value or absent if the property does not
     *         exist
     */
    <T> Optional<T> get(String propertyName, Class<T> type);

    /**
     * Given a property on this resource containing the path of another resource, get an <code>Optional</code>
     * containing the href to the resource (i.e. the content path with ".html" appended).
     *
     * @param propertyName name of property containing a valid content path
     * @return href value wrapped in an <code>Optional</code>
     */
    Optional<String> getAsHref(String propertyName);

    /**
     * Given a property on this resource containing the path of another resource, get an <code>Optional</code>
     * containing the href to the resource.  Use this method with a <code>true</code> argument when appending ".html" to
     * the resource path is desired only for valid CQ pages and not external paths.
     *
     * @param propertyName name of property containing a valid content path
     * @param strict if true, strict resource resolution will be applied and only valid CQ content paths will have
     * ".html" appended
     * @return href value wrapped in an <code>Optional</code>
     */
    Optional<String> getAsHref(String propertyName, boolean strict);

    /**
     * Given a property on this resource containing the path of another resource, get an <code>Optional</code>
     * containing the href to the resource.  Use this method with a <code>true</code> argument when appending ".html" to
     * the resource path is desired only for valid CQ pages and not external paths.  Setting <code>mapped</code> to
     * <code>true</code> will map the path value, if it exists, through the Sling Resource Resolver.
     *
     * @param propertyName name of property containing a valid content path
     * @param strict if true, strict resource resolution will be applied and only valid CQ content paths will have
     * ".html" appended
     * @param mapped if true, the property value will be routed through the Resource Resolver to determine the mapped
     * path for the value.  For example, if a mapping from "/content/" to "/" exists in the Apache Sling Resource
     * Resolver Factory OSGi configuration, getting the mapped href for the path "/content/citytechinc" will return
     * "/citytechinc.html".
     * @return href value wrapped in an <code>Optional</code>
     */
    Optional<String> getAsHref(String propertyName, boolean strict, boolean mapped);

    /**
     * Given a property on this resource containing the path of another resource, get a link to the resource.
     *
     * @param propertyName name of property containing a valid content path
     * @return <code>Optional</code> link object, absent if property does not contain a valid content path
     */
    Optional<Link> getAsLink(String propertyName);

    /**
     * Given a property on this resource containing the path of another resource, get a link to the resource.  Use this
     * method with a <code>true</code> argument when including an extension for the link is desired only for valid CQ
     * pages and not external paths.
     *
     * @param propertyName name of property containing a valid content path
     * @param strict if true, strict resource resolution will be applied and only valid CQ content paths will have an
     * extension
     * @return <code>Optional</code> link object, absent if property does not contain a valid content path
     */
    Optional<Link> getAsLink(String propertyName, boolean strict);

    /**
     * Given a property on this resource containing the path of another resource, get a link to the resource.  Use this
     * method with a <code>true</code> argument when including an extension for the link is desired only for valid CQ
     * pages and not external paths.  Setting <code>mapped</code> to <code>true</code> will map the path value, if it
     * exists, through the Sling Resource Resolver.
     *
     * @param propertyName name of property containing a valid content path
     * @param strict if true, strict resource resolution will be applied and only valid CQ content paths will have an
     * extension
     * @param mapped if true, the property value will be routed through the Resource Resolver to determine the mapped
     * path for the value.  For example, if a mapping from "/content/" to "/" exists in the Apache Sling Resource
     * Resolver Factory OSGi configuration, the <code>Link</code> path will be "/citytechinc" rather than
     * "/content/citytechinc".
     * @return <code>Optional</code> link object, absent if property does not contain a valid content path
     */
    Optional<Link> getAsLink(String propertyName, boolean strict, boolean mapped);

    /**
     * Get a multi-valued property from the current node as a list of the given type.
     *
     * @param propertyName name of multi-valued property
     * @param type property type
     * @param <T> property type
     * @return list of property values or an empty list if the property does not exist
     */
    <T> List<T> getAsList(String propertyName, Class<T> type);

    /**
     * Get a page instance from the value of the given property.  Will return an absent <code>Optional</code> if the
     * path value for the given property name does not resolve to a valid CQ page.
     *
     * @param propertyName property name
     * @return <code>Optional</code> page for property value
     */
    Optional<PageDecorator> getAsPage(String propertyName);

    /**
     * Get the referenced DAM asset path for the default image (named "image") for this component.
     *
     * @return <code>Optional</code> image reference path
     */
    Optional<String> getImageReference();

    /**
     * @param name image name
     * @return <code>Optional</code> image reference path
     */
    Optional<String> getImageReference(String name);

    /**
     * Get the DAM asset rendition path for the default image (named "image") for this component.
     *
     * @param renditionName rendition name for this asset (e.g. "cq5dam.thumbnail.140.100.png")
     * @return <code>Optional</code> image rendition path
     */
    Optional<String> getImageRendition(String renditionName);

    /**
     * @param name image name
     * @param renditionName rendition name for this asset
     * @return <code>Optional</code> image rendition path
     */
    Optional<String> getImageRendition(String name, String renditionName);

    /**
     * @return index in sibling nodes or -1 if resource is null or has null parent node
     */
    int getIndex();

    /**
     * Get the index of this node in sibling nodes, ignoring resource types that do not match the specified value.
     *
     * @param resourceType sling:resourceType to filter on
     * @return index in sibling nodes or -1 if resource is null or has null parent node
     */
    int getIndex(String resourceType);

    /**
     * Get the JCR node for this instance.  This will return an absent <code>Optional</code> if the underlying resource
     * for this instance is synthetic or non-existent.
     *
     * @return <code>Optional</code> node for this resource
     */
    Optional<Node> getNode();

    /**
     * Shortcut for getting current resource path.
     *
     * @return resource path
     */
    String getPath();

    /**
     * Get a list of properties that apply for the given predicate.
     *
     * @param predicate predicate to apply
     * @return filtered list of properties or empty list if no properties of this node apply for the given predicate
     */
    List<Property> getProperties(Predicate<Property> predicate);

    /**
     * Get the underlying resource for this instance.
     *
     * @return current resource
     */
    Resource getResource();

    /**
     * @return true if image has content
     */
    boolean isHasImage();

    /**
     * @param name image name (name of image as defined in dialog)
     * @return true if image has content
     */
    boolean isHasImage(String name);
}
