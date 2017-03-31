
(function()
{

    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

    Alvex.WfTaskList= function(htmlId)
    {

        Alvex.WfTaskList.superclass.constructor.call(this, htmlId);
        this.name = "Alvex.WfTaskList";
        Alfresco.util.ComponentManager.reregister(this);
        this.options = YAHOO.lang.merge(this.options, Alvex.WfTaskList.superclass.options);
        return this;
    };


    YAHOO.extend(Alvex.WfTaskList, Alvex.TaskListHeader,
        {

            createStartWorkflowMenu: Alvex.WfTaskList.prototype.createStartWorkflowMenu = function () {
                this.widgets.startWorkflowButton = Alfresco.util.createYUIButton(this, "startWorkflow-button", this.onStartWorkflowButtonClick,
                    {additionalClass: "alf-primary-button"});
                Dom.removeClass(Selector.query(".hidden", this.id + "-body", true), "hidden");
            },

            onStartWorkflowButtonClick: function WLT_onNewFolder(e, p_obj)
            {
                document.location.href = Alfresco.util.siteURL("start-workflow?referrer=tasks&myTasksLinkBack=true");
            }

        }, true);
})();
