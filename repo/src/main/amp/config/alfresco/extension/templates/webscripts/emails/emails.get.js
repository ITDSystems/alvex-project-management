(function() {
	var folderName = url.templateArgs['folder'];
	model.emails = []
	for each(var e in email.folders[folderName].emails) {
		var atts = []
		for each (var a in e.attachments) {
			atts.push({
				fileName: a.fileName,
				nodeRef: a.node.nodeRef.toString()
			})
		}
		model.emails.push({
			from: e.from,
			to: e.to,
			cc: e.to,
			sentDate: e.sentDate,
			subject: ( e.subject !== null ? e.subject : "" ),
			body: e.body,
			nodeRef: e.node.nodeRef.toString(),
			attachments: atts
		})
	}
})();
