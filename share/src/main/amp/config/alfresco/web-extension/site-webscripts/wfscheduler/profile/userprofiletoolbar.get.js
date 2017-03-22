/**
 * User Profile - Toolbar Component GET method
 */
function main()
{
   // Add Profile link
   addLink("wfscheduler-page", "wfscheduler", "wfscheduler.page.title");

}

main();

function addLink(id, href, msgId, msgArgs)
{
    model.links.push(
        {
            id: id,
            href: href,
            cssClass: (model.activePage == href) ? "theme-color-4" : null,
            label: msg.get(msgId, msgArgs ? msgArgs : null)
        });
}