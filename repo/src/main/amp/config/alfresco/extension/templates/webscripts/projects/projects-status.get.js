(function() {
	try{

		var getNodes = function(folder)
		{
			var nodes = [];
			if( !folder || folder === null )
				return nodes;

			for each(var node in folder.children)
				if( ( node.properties['alvexcm:relationType'] === 'project-workflows' )
							|| ( node.properties['alvexcm:relationType'] === 'case-workflows' ) )
					nodes.push(node);
			for each(var nodeGroup in folder.assocs)
			{
				for each(var node in nodeGroup )
					if( ( node.properties['alvexcm:relationType'] === 'project-workflows' )
							|| ( node.properties['alvexcm:relationType'] === 'case-workflows' ) )
						nodes.push(node);
			}
			return nodes;
		};

		var processSite = function(shortName)
		{
		  var milestones = [];
		  var site = siteService.getSite(shortName);
		  var checklists = site.getContainer("checklists");
		  if (checklists != null) 
		  {
		  	var mls = checklists.children;
		  	for each(var m in mls)
		  	{
				milestones.push(
				{
					"status": m.properties["alvexcm:checkListItemStatus"],
					"summary": m.properties["alvexcm:checkListItemSummary"],
					"dueDate": (m.properties["alvexcm:checkListItemDueDate"] != null ? utils.toISO8601(new Date(m.properties["alvexcm:checkListItemDueDate"])) : "" )
				});
			  }
		  }
		  var workflows = [];
		  var store = companyhome.childrenByXPath('/sys:system/sys:alvex/alvex:data/alvex:project-management/cm:'+shortName)[0];
		  var nodes = getNodes(store);

		  for each(var node in nodes)
		  {
			var workflowInstance = workflowHelper.getWorkflowInstance(node.properties["alvexcm:workflowInstance"])[0];
			workflows.push(
			{
			  "summary": workflowInstance.description,
			  "dueDate": (workflowInstance.dueDate != null ? utils.toISO8601(workflowInstance.dueDate) : "" ),
			  "status": (workflowInstance.isActive ? "pending" : "completed" )
			});
		  }

		  return {"shortName": site.shortName, "title": site.title, "milestones": milestones, "workflows": workflows};
		}

		model.projects = [];
		var sites = siteService.findSites(null, 'project-dashboard', 0);
		for each (var site in sites)
		{
			var members = site.listMembers('','',0,true);
			var checkPermission = false;
  			for (member in members) 
  			{
    			if(member == person.properties.userName) 
    			{
      				checkPermission = true;
       			}
   			}
  			if (checkPermission == true)
    		{
		  		model.projects.push(processSite(site.shortName));
			}
		}
		status.code = 200;
	} catch (e) {
		status.code = 500;
		status.message = e.message;
		model.message = e.message;
	}
})();
