var workflows = [];
var json = remote.call("/api/wfscheduler/wfinfo?user=" + user.name);
if (json.status == 200)
{
    // Create javascript objects from the repo response
    var obj = eval('(' + json + ')');
    if (obj)
    {
        workflows = obj.workflows;
    }
}

var delimeter = {
    name: "alfresco/html/HR",
    config: {
        style: "background-color: #d4d4d4; margin: 20px 10px; height: 1px !important;"
    }
};
var saveButton = {
    name: "wfscheduler/widgets/SaveSchedules",
    config: {
            baseClass: "save-cron-button",
            additionalCssClasses: "call-to-action",
            label: msg.get("save.cron.button"),
            userName: user.name
        }
    };

var widgets = [];
if(workflows.length > 0){
    for(var i=0; i<workflows.length; i++){
        widgets.push(delimeter);
        var widget = {
            id: "wfscheduler-wfinfo-" + i,
            name: "wfscheduler/widgets/Cron",
            config: {
                wfInfo: workflows[i]
            }
        };

        widgets.push(widget);
    }
    widgets.push(saveButton);
}

model.jsonModel = {
    rootNodeId: "wfscheduler",
    services: [],
    widgets: widgets/*{
        name: "alfresco/layout/VerticalWidgets",
        config: {
            widgetMarginTop: 10,
            widgetMarginBottom: 10,
            widgets: widgets
        }
    }*/
};