## Servlets

### Abstract JSON Response Servlet

`com.citytechinc.aem.bedrock.servlets.AbstractJsonResponseServlet`

Servlets should extend this class when writing a JSON response.  Objects passed to any of the `writeJsonResponse()` methods will be serialized to the response writer using the [Jackson](https://github.com/FasterXML/jackson-databind) data binding library.

### Abstract Options Provider Servlet

`com.citytechinc.aem.bedrock.servlets.optionsprovider.AbstractOptionsProviderServlet`

Base class for providing a list of "options" to a component dialog widget.  An option is simply a text/value pair to be rendered in a selection box.  The implementing class determines how these options are retrieved from the repository (or external provider, such as a web service).

### Abstract Validation Servlet

`com.citytechinc.aem.bedrock.servlets.AbstractValidationServlet`

Base class for validating component dialog fields.  Validation business logic is delegated to the extending class via the abstract `isValid()` method.

Servlets extending this class should be annotated with the `@SlingServlet` annotation:

    @SlingServlet(resourceTypes = "projectname/components/content/example", selectors = "validator", extensions = "json", methods = "GET")

The component `dialog.xml` can call the the validator for a dialog field by defining the validator function:

    <name jcr:primaryType="cq:Widget" fieldLabel="Name" name="./name" xtype="textfield"
          validator="function(value) {
              return CITYTECH.Utilities.Dialog.validateField(this, value, 'Name is invalid');
          }" />

### Image Servlet

`com.citytechinc.aem.bedrock.servlets.ImageServlet`

The image servlet overrides CQ's default image rendering servlets to provide image resizing and the ability to associate additional named images to a page or component.

For additional details, see the [Image Rendering](https://github.com/Citytechinc/bedrock/wiki/Image-Rendering) page.

### Paragraph JSON Servlet

`com.citytechinc.aem.bedrock.servlets.paragraphs.ParagraphJsonServlet`

### Selective Replication Servlet

`com.citytechinc.aem.bedrock.servlets.replication.SelectiveReplicationServlet`

This servlet is exposed via the `/bin/replicate/selective` path, which can be called from a JavaScript function to trigger a replication action to a specific set of Replication Agents.

    var path1 = '/content/dam';
    var path2 = '/content/en';

    var params = {
        paths: [path1, path2],
        action: 'ACTIVATE',
        agentIds: ['staging', 'publish']
    };

    $.post('/bin/replicate/selective', $.param(params, true), function (data) {
        if (data[0][path1] == true) {
            alert('Successfully activated ' + path1);
        }

        if (data[1][path2] == true) {
            alert('Successfully activated ' + path2);
        }
    });