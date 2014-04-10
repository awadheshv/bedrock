/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.node;

import com.citytechinc.aem.bedrock.content.link.Link;
import com.citytechinc.aem.bedrock.content.page.PageDecorator;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import java.util.List;

/**
 * Represents a "component" node in the JCR, typically an unstructured node in the hierarchy of a CQ page.
 * <p/>
 * Many methods return an <a href="https://code.google.com/p/guava-libraries/wiki/UsingAndAvoidingNullExplained#Optional">Optional</a>
 * type where a null instance would otherwise be returned.
 */
public interface ComponentNode extends BasicNode {

    /**
     * Find the first ancestor node that matches the given predicate condition.  This respects the same inheritance
     * semantics as the <code>getInherit()</code> methods in that it maintains the same relative path as the current
     * node when traversing through ancestor pages.
     *
     * @param predicate predicate to match ancestor nodes against
     * @return <code>Optional</code> node that matches the predicate condition
     */
    Optional<ComponentNode> findAncestor(Predicate<ComponentNode> predicate);

    /**
     * Get a list of descendant nodes that match the given predicate condition.
     *
     * @param predicate predicate to match descendant nodes against
     * @return list of nodes that match the predicate condition
     */
    List<ComponentNode> findDescendants(Predicate<ComponentNode> predicate);

    /**
     * Find the first ancestor node containing the given property name.
     *
     * @param propertyName property name to find on ancestor nodes
     * @return <code>Optional</code> node that contains the property
     */
    Optional<ComponentNode> findAncestorWithProperty(String propertyName);

    /**
     * Find the first ancestor node where the given property name has the specified value.
     *
     * @param propertyName property name to find on ancestor nodes
     * @param propertyValue value of named property to match
     * @param <T> type of value
     * @return <code>Optional</code> node that contains the property value
     */
    <T> Optional<ComponentNode> findAncestorWithPropertyValue(String propertyName, T propertyValue);

    /**
     * Given a property on this node containing the path of another resource, get the href to the resource, using
     * inheritance if the value does not exist on this component.
     *
     * @param propertyName name of property containing a valid content path
     * @return <code>Optional</code> href
     */
    Optional<String> getAsHrefInherited(String propertyName);

    /**
     * Given a property on this node containing the path of another resource, get the href to the resource, using
     * inheritance if the value does not exist on this component.  Use this method with a <code>true</code> argument
     * when appending ".html" to the resource path is desired only for valid CQ pages and not external paths.
     *
     * @param propertyName name of property containing a valid content path
     * @param strict if true, strict resource resolution will be applied and only valid CQ content paths will have
     * ".html" appended
     * @return <code>Optional</code> href
     */
    Optional<String> getAsHrefInherited(String propertyName, boolean strict);

    /**
     * Given a property on this node containing the path of another resource, get the href to the resource, using
     * inheritance if the value does not exist on this component.  Use this method with a <code>true</code> argument
     * when appending ".html" to the resource path is desired only for valid CQ pages and not external paths.  Setting
     * <code>mapped</code> to <code>true</code> will map the path value, if it exists, through the Sling Resource
     * Resolver.
     *
     * @param propertyName name of property containing a valid content path
     * @param strict if true, strict resource resolution will be applied and only valid CQ content paths will have
     * ".html" appended
     * @param mapped if true, the property value will be routed through the Resource Resolver to determine the mapped
     * path for the value.  For example, if a mapping from "/content/" to "/" exists in the Apache Sling Resource
     * Resolver Factory OSGi configuration, getting the mapped href for the path "/content/citytechinc" will return
     * "/citytechinc.html".
     * @return <code>Optional</code> href
     */
    Optional<String> getAsHrefInherited(String propertyName, boolean strict, boolean mapped);

    /**
     * Given a property on this node containing the path of another resource, get a link to the resource, using
     * inheritance if the value does not exist on this component.
     *
     * @param propertyName name of property containing a valid content path
     * @return <code>Optional</code> link object, or null if the property does not contain a valid content path
     */
    Optional<Link> getAsLinkInherited(String propertyName);

    /**
     * Given a property on this node containing the path of another resource, get a link to the resource, using
     * inheritance if the value does not exist on this component.  Use this method with a <code>true</code> argument
     * when including an extension for the link is desired only for valid CQ pages and not external paths.
     *
     * @param propertyName name of property containing a valid content path
     * @param strict if true, strict resource resolution will be applied and only valid CQ content paths will have an
     * extension
     * @return <code>Optional</code> link object, or null if the property does not contain a valid content path
     */
    Optional<Link> getAsLinkInherited(String propertyName, boolean strict);

    /**
     * Given a property on this node containing the path of another resource, get a link to the resource, using
     * inheritance if the value does not exist on this component.  Use this method with a <code>true</code> argument
     * when including an extension for the link is desired only for valid CQ pages and not external paths.  Setting
     * <code>mapped</code> to <code>true</code> will map the path value, if it exists, through the Sling Resource
     * Resolver.
     *
     * @param propertyName name of property containing a valid content path
     * @param strict if true, strict resource resolution will be applied and only valid CQ content paths will have an
     * extension
     * @param mapped if true, the property value will be routed through the Resource Resolver to determine the mapped
     * path for the value.  For example, if a mapping from "/content/" to "/" exists in the Apache Sling Resource
     * Resolver Factory OSGi configuration, the <code>Link</code> path will be "/citytechinc" rather than
     * "/content/citytechinc".
     * @return <code>Optional</code> link object, or null if the property does not contain a valid content path
     */
    Optional<Link> getAsLinkInherited(String propertyName, boolean strict, boolean mapped);

    /**
     * Get a multi-valued property from the current node as a list of the given type, using inheritance if the value
     * does not exist on this component.
     *
     * @param propertyName name of multi-valued property
     * @param type property type
     * @param <T>
     * @return list of property values or an empty list if the property does not exist
     */
    <T> List<T> getAsListInherited(String propertyName, Class<T> type);

    /**
     * Get a page from the value of the given property, using inheritance if the value does not exist on this component.
     * The property value will be localized to the current page context before getting the page.
     *
     * @param propertyName property name
     * @return <code>Optional</code> page for property value
     */
    Optional<PageDecorator> getAsPageInherited(String propertyName);

    /**
     * Get the component node for the resource at the given path relative to the current node.
     *
     * @param relativePath relative path to component
     * @return <code>Optional</code> node for component
     */
    Optional<ComponentNode> getComponentNode(String relativePath);

    /**
     * Get a list of child nodes for the current node.
     *
     * @return list of component nodes
     */
    List<ComponentNode> getComponentNodes();

    /**
     * Get a predicate-filtered list of child nodes for the current node.
     *
     * @param predicate predicate used to filter nodes
     * @return list of component nodes that meet the predicate criteria
     */
    List<ComponentNode> getComponentNodes(Predicate<ComponentNode> predicate);

    /**
     * Get a list of child nodes for the resource at the given path relative to this node.
     *
     * @param relativePath relative path to parent of desired nodes
     * @return list of component nodes below the specified relative path
     */
    List<ComponentNode> getComponentNodes(String relativePath);

    /**
     * Get a list of child nodes for the resource at the given path relative to this node, returning only the nodes that
     * have the specified resource type.
     *
     * @param relativePath relative path to parent of desired nodes
     * @param resourceType sling:resourceType of nodes to get from parent node
     * @return list of component nodes matching the given resource type below the specified relative path
     */
    List<ComponentNode> getComponentNodes(String relativePath, String resourceType);

    /**
     * Get a list of child nodes for the resource at the given path relative to this node, returning only the nodes that
     * meet the predicate criteria.
     *
     * @param relativePath relative path to parent of desired nodes
     * @param predicate predicate used to filter nodes
     * @return list of component nodes that meet the predicate criteria below the specified relative path
     */
    List<ComponentNode> getComponentNodes(String relativePath, Predicate<ComponentNode> predicate);

    /**
     * Get the design node for the current component.
     *
     * @return <code>Optional</code> design node for the current style
     */
    Optional<BasicNode> getDesignNode();

    /**
     * @return <code>Optional</code> inherited image reference
     */
    Optional<String> getImageReferenceInherited();

    /**
     * @param name image name
     * @return <code>Optional</code> inherited image reference
     */
    Optional<String> getImageReferenceInherited(String name);

    /**
     * Get the image source for the default image (named "image") for this node. Uses the image servlet rather than a
     * direct reference to the DAM path.
     *
     * @return optional inherited image source
     */
    Optional<String> getImageSourceInherited();

    /**
     * Get the image source for the default image (named "image") for this node for the given width.
     *
     * @param width image width
     * @return optional inherited image source
     */
    Optional<String> getImageSourceInherited(int width);

    /**
     * Get the image source for this node for the named image.
     *
     * @param name image name (name of image as defined in dialog)
     * @return optional inherited image source
     */
    Optional<String> getImageSourceInherited(String name);

    /**
     * Get the image source for this node for the named image and given width.
     *
     * @param name image name (name of image as defined in dialog)
     * @param width image width
     * @return optional inherited image source
     */
    Optional<String> getImageSourceInherited(String name, int width);

    /**
     * Get a property value from the current node. If no value is found, recurse up the content tree respective to the
     * page and relative node path until a value is found.
     *
     * @param <T> result type
     * @param propertyName property to get
     * @param defaultValue value if no result is found
     * @return inherited value
     */
    <T> T getInherited(String propertyName, T defaultValue);

    /**
     * Get a property value from the current node.   If no value is found, recurse up the content tree respective to the
     * page and relative node path until a value is found, returning an absent <code>Optional</code> if not.  This
     * returns the same value as the underlying <code>ValueMap</code> wrapped in an <code>Optional</code> instance
     * instead of returning null.
     *
     * @param propertyName property name
     * @param type property type
     * @param <T> type
     * @return <code>Optional</code> of the given type containing the property value or absent if no value is found
     */
    <T> Optional<T> getInherited(String propertyName, Class<T> type);

    /**
     * Get the children of a node relative to the current node. If node does not exist relative to current page, inherit
     * from a parent page.
     *
     * @param relativePath path relative to current node
     * @return list of nodes representing children of the addressed node or inherited from a parent page
     */
    List<BasicNode> getNodesInherited(String relativePath);

    /**
     * Get the parent of this node.
     *
     * @return parent component node or absent optional if resource has no parent
     */
    Optional<ComponentNode> getParent();
}
