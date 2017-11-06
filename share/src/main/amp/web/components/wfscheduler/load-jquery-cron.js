window.onload = function() {
    var imported = document.createElement('script');
    imported.src = Alfresco.constants.URL_RESCONTEXT + 'components/wfscheduler/jquery-cron.js';
    document.head.appendChild(imported);
};