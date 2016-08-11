<#escape x as jsonUtils.encodeJSONString(x)>
{
	<#if message?has_content>
	"message": "${message}",
	</#if>
    "data":
    [
    <#list projects as p>
        {
            "shortName": "${p.shortName}",
            "title": "${p.title}",
            "workflows":
            [
            <#list p.workflows as w>
                {
                    "summary": "${w.summary}",
                    "status": "${w.status}",
                    "dueDate": "${w.dueDate}"
                }<#if w_has_next>,</#if>
            </#list>
            ],
            "milestones":
            [
            <#list p.milestones as m>
                {
                    "summary": "${m.summary}",
                    "status": "${m.status}",
                    "dueDate": "${m.dueDate}"
                }<#if m_has_next>,</#if>
            </#list>
            ]
        }<#if p_has_next>,</#if>
    </#list>
    ]
}
</#escape>