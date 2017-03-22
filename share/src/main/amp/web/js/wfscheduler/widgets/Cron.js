define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "alfresco/core/Core",
        "alfresco/core/CoreWidgetProcessing",
        "dijit/_TemplatedMixin",
        "dojo/text!./templates/Cron.html",
        "dojo/on",
        "dojo/_base/lang",
        "cron"
    ],
    function(declare, _Widget, Core, CoreWidgetProcessing, _Templated, template, on, lang, cron) {
        return declare([_Widget, Core, CoreWidgetProcessing, _Templated], {
            templateString: template,
            i18nRequirements: [ {i18nFile: "./i18n/Cron.properties"} ],
            cssRequirements: [{cssFile:"./css/Cron.css"}],

            wfInfo: null,

            postMixInProperties: function wfscheduler_widgets_Cron_postMixInProperties() {
                this.deleteCron = this.message("delete.button");
            },

            postCreate: function wfscheduler_widgets_Cron_postCreate(){
                if(this.wfInfo != null){
                    this.headerNode.innerText = this.wfInfo.workflowId.title + "(" + this.wfInfo.workflowId.description + ")";
                    this.assigneeNode.innerText = this.message("cron.assignee") + this.wfInfo.workflowId.assignee;
                    $(this.cronNode).cron();
                    $(this.cronNode).cron("value", this.wfInfo.cron);
                    on(this.deleteButton, "click", lang.hitch(this, this.onClickDeleteCron));
                }

                this.inherited(arguments);
            },

            onClickDeleteCron: function wfscheduler_widgets_Cron_onClickDeleteCron(event){
                dijit.getEnclosingWidget(this.domNode).destroy();
            }
        });
});