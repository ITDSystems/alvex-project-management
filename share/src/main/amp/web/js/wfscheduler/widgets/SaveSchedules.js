define(["dojo/_base/declare",
        "alfresco/buttons/AlfButton",
        "alfresco/core/CoreXhr",
        "service/constants/Default",
        "dojo/dom-construct",
        "dojo/_base/lang",
        "dojo/string"
    ],
    function(declare, AlfButton, AlfCoreXhr, AlfConstants, domConstruct, lang, string) {
        return declare([AlfButton, AlfCoreXhr], {
            cssRequirements: [{cssFile:"./css/SaveSchedule.css"}],

            UPDATE_ALL_JOBS: AlfConstants.PROXY_URI + "wfscheduler/update-jobs?user=${user}",

            userName: null,

            postCreate: function wfscheduler_widgets_SaveSchedules_postCreate(){
                this.inherited(arguments);

                this.infoMessage = domConstruct.create("div", {
                    id:"infoMessage",
                    className:"info-container"
                },this.domNode, "first");
            },

            onClick: function wfscheduler_widgets_SaveSchedules_onClick(){
                //dijit.getEnclosingWidget(this.domNode).destroy();
                var crons = document.getElementsByClassName("wfscheduler-wfInfo-cron");
                if(crons.length < 1){
                    this.displayMessage("Not found data for save");
                }
                var wfs = [];
                for(var i=0; i<crons.length; i++){
                    var widget = dijit.getEnclosingWidget(crons[i]);
                    var wfInfo = widget.wfInfo;
                    wfInfo.cron = $(widget.cronNode).cron("value");
                    wfs.push(wfInfo);
                }


                var url = string.substitute(this.UPDATE_ALL_JOBS, {
                    user: this.userName
                });

                var config = {
                    url: url,
                    method: "POST",
                    data: wfs,
                    successCallback: this.onDataLoadSuccess,
                    callbackScope: this,
                    failureCallback: this.onDataLoadFailure
                };

                this.serviceXhr(config);
            },

            onDataLoadSuccess: function wfscheduler_widgets_SaveSchedules_onDataLoadSuccess(payload){
                var status = payload.success;

                if(status){
                    this.displayMessage("Save complete!", status)
                }else{
                    this.displayMessage("Save failure", status);
                }
            },

            onDataLoadFailure: function wfscheduler_widgets_SaveSchedules_onDataLoadFailure(payload){
                this.displayMessage("Save failure", false);
            },

            displayMessage: function wfscheduler_widgets_SaveSchedules_displayMessage(msg, success) {
                if (!msg) {
                    return null;
                }
                this.infoMessage.innerText = msg;
                if(success){
                    $(this.infoMessage).removeClass("failure");
                    $(this.infoMessage).addClass("success");
                }else{
                    $(this.infoMessage).removeClass("success");
                    $(this.infoMessage).addClass("failure");
                }

                $(this.infoMessage).fadeIn(300);
                setTimeout(lang.hitch(this, function(){
                    $(this.infoMessage).fadeOut(300);
                }), 1000);
            },
        });
    });