<#include "../../../org/alfresco/include/alfresco-template.ftl" />
<#include "../../../org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader />
<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
   </div>
   </@>
   <@markup id="bd">
   <div id="bd">
      <@region id="toolbar" scope="template" />
      <@region id="wfscheduler" scope="template"/>
   </div>
   </@>
</@>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global"/>
   </div>
   </@>
</@>
