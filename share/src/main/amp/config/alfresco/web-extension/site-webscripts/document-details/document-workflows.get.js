<import resource="classpath:alfresco/templates/org/alfresco/import/alfresco-util.js">

    function getDocumentWorkflows(nodeRef)
    {
       var result = remote.call("/api/alvex/node/" + nodeRef.replace(":/", "") + "/workflow-instances");
       if (result.status != 200)
       {
          AlfrescoUtil.error(result.status, 'Could not load document workflows for ' + nodeRef);
       }
       return eval('(' + result + ')');
    }

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
   if (documentDetails)
   {
      model.destination = documentDetails.item.parent.nodeRef
      var result = getDocumentWorkflows(model.nodeRef);
      model.activeWorkflows = result.active;
      model.completedWorkflows = result.completed;
   }

   // Widget instantiation metadata...
   var documentWorkflows = {
      id : "DocumentWorkflows",
      name : "Alfresco.DocumentWorkflows",
      options : {
         nodeRef : model.nodeRef,
         siteId : model.site,
         destination : model.destination
      }
   };
}

main();