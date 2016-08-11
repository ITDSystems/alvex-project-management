(function() {
	try{
		// Request parameters
		var projectId = decodeURIComponent(url.templateArgs['projectId']);
		var site = siteService.getSite(projectId);
		var cont = site.getContainer("conversations");
		if( cont === null )
			cont = site.createContainer("conversations");
		model.ref = cont.nodeRef.toString();
		status.code = 200;
	} catch (e) {
		status.code = 500;
		status.message = e.message;
		model.message = e.message;
	}
})();