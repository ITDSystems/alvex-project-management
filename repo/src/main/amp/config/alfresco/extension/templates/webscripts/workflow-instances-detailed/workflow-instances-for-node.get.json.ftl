<#-- Workflow Instances collection for NodeRef-->

<#import "/org/alfresco/repository/workflow/workflow.lib.ftl" as workflowLib/>
{
   "active":
   [
      <#if activeWorkflowInstances??>
      <#list activeWorkflowInstances as workflowInstance>
      <@workflowLib.workflowInstanceJSON workflowInstance=workflowInstance />
      <#if workflowInstance_has_next>,</#if>
      </#list>
      </#if>
   ],
   "completed":
   [
      <#if completedWorkflowInstances??>
      <#list completedWorkflowInstances as workflowInstance>
      <@workflowLib.workflowInstanceJSON workflowInstance=workflowInstance />
      <#if workflowInstance_has_next>,</#if>
      </#list>
      </#if>
   ]
}

