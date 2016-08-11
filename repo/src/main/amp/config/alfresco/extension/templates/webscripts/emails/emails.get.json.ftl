 <#escape x as jsonUtils.encodeJSONString(x)>
 {
 	"data": [
 	<#list emails as e>
 	{
 		"from": "${e.from}",
 		"to": [
        <#list e.to as t>
            "${t}"<#if t_has_next>,</#if>
        </#list>
        ],
 		"cc": [
        <#list e.cc as c>
            "${c}"<#if c_has_next>,</#if>
        </#list>
        ],
 		"subject": "${e.subject}",
 		"body": "${e.body}",
        "sentDate": "${e.sentDate?string("d/M/Y")}",
        "nodeRef": "${e.nodeRef}",
 		"attachments": [
 		<#list e.attachments as a>
 		{
 			"fileName": "${a.fileName}",
 			"nodeRef": "${a.nodeRef}"
 		}<#if a_has_next>,</#if>
 		</#list>
 		]
 	}<#if e_has_next>,</#if>
 	</#list>
 	]
 }
 </#escape>