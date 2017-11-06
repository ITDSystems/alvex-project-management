
(function()
{

    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

    Alfresco.component.WfSchedulerStartWorkflow = function(htmlId)
    {

        Alfresco.component.WfSchedulerStartWorkflow.superclass.constructor.call(this, htmlId);
        this.name = "Alfresco.component.WfSchedulerStartWorkflow";
        Alfresco.util.ComponentManager.reregister(this);
        this.options = YAHOO.lang.merge(this.options, Alvex.StartWorkflow.superclass.options);
        return this;
    };


    YAHOO.extend(Alfresco.component.WfSchedulerStartWorkflow, Alvex.StartWorkflow,
        {

            onReady: function StartWorkflow_onReady()
            {
                Alfresco.component.WfSchedulerStartWorkflow.superclass.onReady.call(this);
            },

            /**
             * Called when a workflow form has been loaded.
             * Will insert the form in the Dom.
             *
             * @method onWorkflowFormLoaded
             * @param response {Object}
             */
            onWorkflowFormLoaded: function CustomStartWorkflow_onWorkflowFormLoaded(response)
            {
                var formEl = Dom.get(this.id + "-workflowFormContainer");
                Dom.addClass(formEl, "hidden");
                formEl.innerHTML = getWfCheckBox() + response.serverResponse.responseText;

                var button = document.createElement('div')
                button.id = this.id + "-wfScheduler-button"
                var buttonContainer = document.getElementsByClassName('form-buttons')[0];
                buttonContainer.appendChild(button);
                Alfresco.util.createYUIButton(this, "wfScheduler-button", this.onWokflowClick,{
                    label: this.msg("sheduler.button.name"),
                    title: this.msg("sheduler.button.name"),
                    className: "wfscheduler-button hidden"
                });

                this.wfSchedulerField = getField(this.msg("sheduler.field.name"));
                var filedContainer = document.getElementsByClassName('form-fields')[0];
                filedContainer.appendChild(this.wfSchedulerField);
                jQuery('#cronField').cron();

                this.options.selectActivity = this.getWorkflowDef(response.config.dataObj);
            },

            getWorkflowDef: function CustomStartWorkflow_getWorkflowDef(dataObj) {
                var defs = this.options.workflowDefinitions;
                for(var def in defs){
                    if(dataObj.itemId == defs[def].name){
                        dataObj.title = defs[def].title;
                        dataObj.description = defs[def].description;
                    }
                }

                return dataObj;
            },

            onWokflowClick: function CustomStartWorkflow_onWokflowClick(event){
                var form = event.target.form;
                if (form == null) return;

                var subminEvent = {
                    type: "submit"
                }

                var formComponent = Alfresco.util.ComponentManager.get(form.id);
                if (formComponent == null && formComponent.formsRuntime) return;

                var formsRuntime = formComponent.formsRuntime;
                formsRuntime._setAllFieldsAsVisited();

                if(formsRuntime._runValidations(subminEvent, null, 3)){


                    var dataObj = buildAjaxForSubmit(form);
                    var cron = $('#cronField').cron('value');
                    if(cron != null && cron != ''){
                        dataObj['cron'] = cron;
                        dataObj['timeZoneOffset'] = new Date().getTimezoneOffset();
                        dataObj['workflowId'] = this.options.selectActivity;

                        var assignee = document.getElementsByClassName("itemtype-cm:person");
                        if(assignee.length == 0){
                            assignee = document.getElementsByClassName("itemtype-cm:authorityContainer");
                            if(assignee.length != 0){
                                dataObj['workflowId'].assignee = assignee[0].innerText;
                            }
                        } else {
                            dataObj['workflowId'].assignee = assignee[0].innerText;
                        }

                        var url = Alfresco.constants.PROXY_URI + "wfscheduler/write-job";
                        Alfresco.util.Ajax.jsonRequest(
                            {
                                method: Alfresco.util.Ajax.POST,
                                url: url,
                                dataObj: dataObj,
                                successCallback:
                                    {
                                        fn: this.writeJobInfoResult,
                                        scope: this
                                    }
                            });
                    } else {
                        Alfresco.util.PopupManager.displayPrompt(
                            {
                                title: this.msg("cron.deffered.title.failure"),
                                text: this.msg("cron.deffered.message.failure")
                            });
                    }
                }
                else
                {
                    // stop the event from continuing and sending the form.
                    Event.stopEvent(event);

                    if (Alfresco.logger.isDebugEnabled())
                        Alfresco.logger.debug("Submission prevented as validation failed");

                    // Enable submit buttons
                    this._toggleSubmitElements(true);
                }
            },

            writeJobInfoResult: function writeJobInfoResult(result){
                var res = result.json;
                if(res != null && res.success){
                    window.location.reload();
                } else {
                    Alfresco.util.PopupManager.displayPrompt(
                        {
                            title: this.msg("cron.write.failure.title"),
                            text: this.msg("cron.write.failure.text")
                        });
                }
            }
        }, true);
})();

function getWfCheckBox(){
    return '<div id="wfScheduler-activator">' +
        '<input class="formsCheckBox" id="wfSheduler-checkbox" type="checkbox" tabindex="0" name="-" value="true" onchange="disableContainer(this)">' +
        '<label for="wfSheduler-checkbox" class="checkbox">' + this.msg("sheduler.title") + '</label></div>';
}

function createCalendar(){
    var calendar = document.createElement('div');
    calendar.id = 'calContainer';
    return calendar;
}

function disableContainer(element){
    var container = document.getElementById('wfSchedulerContainer');
    var button = document.getElementsByClassName("wfscheduler-button")[0];
    if(element.checked){
        button.classList.remove("hidden");
        container.classList.remove("hidden");
    } else {
        button.classList.add("hidden");
        container.classList.add("hidden");
    }
}

function getField(nameField){
    var field = document.createElement('div');
    field.id = "wfSchedulerContainer";
    field.classList.add('set', 'hidden');
    field.innerHTML = '<div class="set-title">' + nameField + '</div><div id="cronField" class="form-field"></div><div id="wfDateSetter"></div>';
    return field;
}

function buildAjaxForSubmit(form)
{
    if (form !== null)
    {
        var formData = {},
            length = form.elements.length;

        for (var i = 0; i < length; i++)
        {
            var element = form.elements[i],
                name = element.name;

            if (name == "-" || name.startsWith("cron") || element.disabled || element.type === "button")
            {
                continue;
            }
            if (name == undefined || name == "")
            {
                name = element.id;
            }
            var value = (element.type === "textarea") ? element.value : YAHOO.lang.trim(element.value);
            if (name)
            {
                // check whether the input element is an array value
                if ((name.length > 2) && (name.substring(name.length - 2) == '[]'))
                {
                    name = name.substring(0, name.length - 2);
                    if (formData[name] === undefined)
                    {
                        formData[name] = new Array();
                    }
                    formData[name].push(value);
                }
                // check whether the input element is an object literal value
                else if (name.indexOf(".") > 0)
                {
                    var names = name.split(".");
                    var obj = formData;
                    var index;
                    for (var j = 0, k = names.length - 1; j < k; j++)
                    {
                        index = names[j];
                        if (obj[index] === undefined)
                        {
                            obj[index] = {};
                        }
                        obj = obj[index];
                    }
                    obj[names[j]] = value;
                }
                else if (!((element.type === "checkbox" || element.type === "radio") && !element.checked))
                {
                    if (element.type == "select-multiple")
                    {
                        for (var j = 0, jj = element.options.length; j < jj; j++)
                        {
                            if (element.options[j].selected)
                            {
                                if (formData[name] == undefined)
                                {
                                    formData[name] = new Array();
                                }
                                formData[name].push(element.options[j].value);
                            }
                        }
                    }
                    else
                    {
                        formData[name] = value;
                    }
                }
            }
        }

        return formData;
    }
}