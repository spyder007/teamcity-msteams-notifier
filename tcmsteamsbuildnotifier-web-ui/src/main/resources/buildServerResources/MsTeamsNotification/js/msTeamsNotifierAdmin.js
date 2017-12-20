var MsTeamsNotifierAdmin = {

    validate : function(){
        try{
            var token = document.forms["msTeamsNotifierAdminForm"]["token"].value;
            var iconUrl = document.forms["msTeamsNotifierAdminForm"]["iconUrl"].value;
            var botName = document.forms["msTeamsNotifierAdminForm"]["botName"].value;
            var maxCommitsToDisplay = document.forms["msTeamsNotifierAdminForm"]["maxCommitsToDisplay"].value;
            var showCommits = document.forms["msTeamsNotifierAdminForm"]["showCommits"].checked;
            var proxyHost = document.forms["msTeamsNotifierAdminForm"]["proxyHost"].value;
            var proxyPort = document.forms["msTeamsNotifierAdminForm"]["proxyPort"].value;
            var proxyUser = document.forms["msTeamsNotifierAdminForm"]["proxyUser"].value;
            var proxyPassword = document.forms["msTeamsNotifierAdminForm"]["proxyPassword"].value;
            var errors = [];

            if(!token){
                errors.push("Api token is required.");
            }
            if(!iconUrl){
                errors.push("Icon url is required.");
            }
            if(!botName){
                errors.push("Bot name is required.");
            }
            if(proxyHost && !proxyPort){
                errors.push("Proxy port is required if a host is specified.");
            }
            if(proxyUser && !proxyPassword){
                errors.push("Proxy password is required if a user is specified.");
            }
            if(!showCommits){
                if(!maxCommitsToDisplay){
                    errors.push("Max commits to display is required.");
                }
                else if(parseInt(maxCommitsToDisplay) == Number.NaN){
                    errors.push("Max commits to display must be a valid integer");
                }
            }

            $('msTeamsNotificationErrors').innerHTML = '';


            if(errors.length > 0) {
                var errorList = jQuery('<ul></ul>');

                jQuery.each(errors, function(index, error) {
                   var li = jQuery('<li/>')
                        .addClass('msteams-config-error')
                        .text(error)
                        .appendTo(errorList);
                });
                errorList.appendTo($('msTeamsNotificationErrors'));
                return false;
            }
            else {
                return true;
            }
        }
        catch(err){
            $('msTeamsNotificationErrors').innerHTML = 'Oops! Something went wrong!';
            return false;
        }
    },

    sendTestNotification : function() {
        if(!MsTeamsNotifierAdmin.validate()) {
            return false;
        }

        jQuery.ajax(
            {
                url: $("msTeamsNotifierAdminForm").action,
                data: {
                    test: 1,
                    token: $("token").value,
                    botName: $("botName").value,
                    iconUrl: $("iconUrl").value,
                    maxCommitsToDisplay: $("maxCommitsToDisplay").value,
                    showCommits: $("showCommits").checked,
                    showCommitters: $("showCommitters").checked,
                    showElapsedBuildTime: $("showElapsedBuildTime").checked,
                    showBuildAgent: $("showBuildAgent").checked,
                    showFailureReason: $("showFailureReason").checked,
                    proxyHost: $("proxyHost").value,
                    proxyPort: $("proxyPort").value,
                    proxyUser: $("proxyUser").value,
                    proxyPassword: $("proxyPassword").getEncryptedPassword($("publicKey").value)
                },
                type: "GET"
            }).done(function() {
                alert("Notification sent\r\n\r\nNote: Any changes have not yet been saved.");
            }).fail(function() {
                alert("Failed to send notification!")
            });

        return false;
    },

    save : function() {
        if(!MsTeamsNotifierAdmin.validate()) {
            return false;
        }

        jQuery.ajax(
            {
                url: $("msTeamsNotifierAdminForm").action,
                data: {
                    edit: 1,
                    token: $("token").value,
                    botName: $("botName").value,
                    iconUrl: $("iconUrl").value,
                    maxCommitsToDisplay: $("maxCommitsToDisplay").value,
                    showCommits: $("showCommits").checked,
                    showCommitters: $("showCommitters").checked,
                    showElapsedBuildTime: $("showElapsedBuildTime").checked,
                    showBuildAgent: $("showBuildAgent").checked,
                    showFailureReason: $("showFailureReason").checked,
                    proxyHost: $("proxyHost").value,
                    proxyPort: $("proxyPort").value,
                    proxyUser: $("proxyUser").value,
                    proxyPassword: $("proxyPassword").getEncryptedPassword($("publicKey").value)
                },
                type: "POST"
            }).done(function() {
                BS.reload();
            }).fail(function() {
                alert("Failed to save configuration!")
            });

        return false;
    }
}