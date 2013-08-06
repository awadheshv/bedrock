Namespace.create('CITYTECH.Utilities.Dialog');

/**
 * A set of utilities to create and dialogs
 * @class CITYTECH.Utilities.Dialog
 */
CITYTECH.Utilities.Dialog = {

    /**
     * Create a new dialog.
     * @methodOf CITYTECH.Utilities.Dialog
     * @name createDialog
     * @param pathToDialog  path to the xml file defining the dialog layout.
     * @param {Function} success function to run on success response from dialog save
     * @param {Function} failure function to run on failure response from dialog save
     * @returns a CQ dialog object.
     */
    createDialog: function (pathToDialog, success, failure) {
        // set up dialog configuration
        var dialogConfig = CQ.WCM.getDialogConfig(pathToDialog);

        if (success) {
            dialogConfig.success = success;
        } else {
            dialogConfig.success = function (form, action) {
                // reload page on dialog success
                CQ.Util.reload(CQ.WCM.getContentWindow());
            };
        }

        if (failure) {
            dialogConfig.failure = failure;
        } else {
            dialogConfig.failure = function (form, action) {
                // alert user when dialog fails
                var resp = CQ.HTTP.buildPostResponseFromHTML(action.response);
                CQ.Ext.Msg.alert(resp.headers[CQ.HTTP.HEADER_MESSAGE]);
            };
        }

        // get dialog
        var dialog = CQ.WCM.getDialog(dialogConfig, pathToDialog);
        
        return dialog;
    },

    /**
     * Populate a dialog with properties and show it.
     * @methodOf CITYTECH.Utilities.Dialog
     * @name populateAndShowDialog
     * @param dialog            dialog object to show.
     * @param pathToProperties  path to properties to use to populate dialog.
     */
    populateAndShowDialog: function (dialog, pathToProperties) {
        dialog.loadContent(pathToProperties);
        dialog.show();
    },

    /**
     * Create and show a new dialog.
     * @methodOf CITYTECH.Utilities.Dialog
     * @name createDialog
     * @param {String} pathToDialog path to the xml file defining the dialog layout.
     * @param {String} pathToProperties  path to where dialog properties are stored.
     */
    loadDialog: function (pathToDialog, pathToProperties) {
        var dialog = createDialog(pathToDialog);
        populateAndShowDialog(dialog, pathToProperties);
    },

    /**
     *  Create a new dialog accessible through the sidekick
     *
     * @methodOf CITYTECH.Utilities.Dialog
     * @name createSidekickDialog
     * @param {String} pathToDialog path to the xml file defining the dialog layout.
     * @param {String} relPathToProperties relative path (starting with '/') to where dialog properties are stored.
     * @param {String} sidekickButtonText text for sidekick button that opens this dialog.
     * @param {Integer} index [optional] index of where to place this dialog's button in sidekick button list, defaults to end of list.
     */
    createSidekickDialog: function (pathToDialog, relPathToProperties, sidekickButtonText, index) {
        var sidekickAction = {
            context: CQ.wcm.Sidekick.PAGE,
            text: sidekickButtonText,
            handler: function () {
                loadDialog(pathToDialog, this.getPath() + relPathToProperties);
            }
        };

        // apply custom sidekick action to sidekick
        var topWindow = CQ.WCM.getTopWindow();
        var defaultActions = topWindow ? topWindow.CQ.wcm.Sidekick.DEFAULT_ACTIONS : CQ.wcm.Sidekick.DEFAULT_ACTIONS;

        // clobber any other actions with the same text and context
        for (var i = defaultActions.length - 1; i >= 0; i--) {
            var action = defaultActions[i];
            if (action.context === sidekickAction.context && action.text === sidekickAction.text) {
                defaultActions.splice(i, 1);
            }
        }

        if (typeof index !== 'undefined') {
            // index has been specified, splice button into correct location
            defaultActions.splice(index, 0, sidekickAction);
        } else {
            // no index, push it to end of sidekick actions
            defaultActions.push(sidekickAction);
        }
    }
};