### Define Objects

Add to and/or override attributes set in page context for use in JSPs.

    <ct:defineObjects/>

This tag is already defined in the CQ library `global.jsp`, so most projects should just include this file in component JSPs rather than using the tag directly.

    <%@include file="/apps/cq-library/components/global.jsp"%>

In addition to the attributes provided by the CQ5 `global.jsp`, the following attributes are also set in page context.

Name | Description
:----|:-----------
isAuthor | Is author environment?
isPublish | Is publish environment?
isEditMode | Is edit mode?
isDesignMode | Is design mode?
isPreviewMode | Is preview mode?
isDebug | Is request parameter debug=true?
componentRequest | `ComponentRequest` object for current component
componentNode | `ComponentNode` object for current component
pageManager | `PageManagerDecorator`, replaces default `PageManager` object
currentPage | `PageDecorator` for current page, replaces default WCM `Page` object

### Disable Author

Force "disabled" WCM mode in the tag body.  Useful for removing authoring capabilities in nested components.

    <ct:disableAuthor>
        ...
    </ct:disableAuthor>

### Title

Render the title for the current page.

Attribute | Required | Description
:---------|:---------|:-----------
propertyName | false | Name of property containing the title to display.  Defaults to "jcr:title".
suffix | false | Value to append after the title property value.

    <head>
        <ct:title propertyName="pageTitle" suffix=" | CITYTECH, Inc."/>
    </head>

### Keywords

Render the keywords for the current page.

    <head>
        <ct:keywords/>
    </head>

### Description

Render the description for the current page.

Attribute | Required | Description
:---------|:---------|:-----------
propertyName | false | Name of property containing the description to display.  Defaults to "jcr:description".
suffix | false | Value to append after the description property value.

    <head>
        <ct:description/>
    </head>

### Component

Instantiates a Component class and sets it in JSP page context.

Attribute | Required | Description
:---------|:---------|:-----------
className | true | Name of component class to instantiate.
name | true | Name to use for page context attribute containing the instantiated component class.
scope | false | JSP scope for component in page context.  Defaults to "page".  Must be one of "page", "request", "session", or "application".

    <ct:component className="com.citytechinc.cq.components.Navigation" name="navigation" scope="request"/>

### Serialize JSON

Serializes a component class or object instance as JSON.

Attribute | Required | Description
:---------|:---------|:-----------
className | false | Name of component class to instantiate.
instanceName | false | Name of existing component or object in page context.  "className" attribute is checked first.
name | false | Name to use for page context attribute containing the instantiated component class.  Only applies when "className" is set.
scope | false | JSP scope for class in page context.  Defaults to "page".  Must be one of "page", "request", "session", or "application".

    <ct:serializeJson className="com.citytechinc.cq.components.Navigation" name="navigation" scope="request"/>

or

    <ct:serializeJson instanceName="navigation"/>

### Property

Render a page or component property value for the current component.

Attribute | Required | Description
:---------|:---------|:-----------
propertyName | true | Name of property containing the value to display.
defaultValue | false | Value to display if the named property does not exist on the current component.
escapeXml | false | If "false", XML property values will not be escaped.  This should be set to "false" if the property value may contain markup (e.g. rich text editor).  Defaults to "true".
inherit | false | If "true", value will be inherited from ancestor components before returning the default value if not found.  Defaults to "false".

    <ct:property propertyName="heading" defaultValue="Heading" escapeXml="false" inherit="true"/>

### Property Boolean

Render a boolean property value for the current component, optionally setting arbitrary string values to use instead of "true" and "false".

Attribute | Required | Description
:---------|:---------|:-----------
propertyName | true | Name of property containing the boolean value to display.
defaultValue | false | Value to display if the named property does not exist on the current component.
true | false | Value to display instead of "true" if the property value is "true", e.g. "Yes".
false | false | Value to display instead of "false" if the property value is "false", e.g. "No".
inherit | false | If "true", value will be inherited from ancestor components before returning the default value if not found.  Defaults to "false".

    <ct:propertyBoolean propertyName="isSelected" defaultValue="true" true="Yes" false="No"/>

### Href

Render an `href` attribute value for the given property name from the current component.

Attribute | Required | Description
:---------|:---------|:-----------
propertyName | true | Name of property containing a path or external URL value to display.
defaultValue | false | Value to display if the named property does not exist on the current component.
inherit | false | If "true", value will be inherited from ancestor components before returning the default value if not found.  Defaults to "false".

    <a href="<ct:href propertyName="path" defaultValue="/page.html" inherit="true"/>">Link</a>

### Image

Draw an HTML image tag for the current component.  See [Image](http://dev.day.com/docs/en/cq/current/javadoc/com/day/cq/wcm/foundation/Image.html) Javadoc for details.  If the image has no content, an image tag is not drawn.

Attribute | Required | Description
:---------|:---------|:-----------
name | false | Name of image for the current component.  Defaults to "image".
alt | false | Image alt text.
title | false | Image title.

    <ct:image name="thumbnail" alt="Thumbnail" title="Thumbnail"/>

### Image Source

Render an image source path for the current component.  The resulting URL triggers the CQ Library Image Servlet rather than referencing the DAM asset path directly.

Attribute | Required | Description
:---------|:---------|:-----------
name | true | Name of image for the current component.  Defaults to "image".
width | false | Width in pixels to render the image.  The width value will be used as a selector in the image servlet URL to resize the image.
defaultValue | false | Image source to display if the named image does not exist on the current component.  Defaults to "".
inherit | false | If "true", image source will be inherited from ancestor components before returning the default value if not found.  Defaults to "false".

    <img src="<ct:imageSource name="thumbnailImage" width="500"/>">