<script type="text/javascript">
	new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
</script>

<div class="dashlet">
	<div class="title">${msg('alvex.projects-summary.label.title')}</div>
	<div class="body scrollableList">
		<div id="${args.htmlid}-dataTable">
		</div>
	</div>
</div>

<script type="text/javascript">//<![CDATA[
	new Alvex.ProjectsSummary("${args.htmlid}").setOptions({
	}).setMessages(
		${messages}
	);
//]]>
</script>
