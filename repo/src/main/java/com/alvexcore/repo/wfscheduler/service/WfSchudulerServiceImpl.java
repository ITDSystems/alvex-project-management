package com.alvexcore.repo.wfscheduler.service;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.forms.FormData;
import org.alfresco.repo.forms.Item;
import org.alfresco.repo.forms.processor.FormProcessor;
import org.alfresco.repo.forms.processor.FormProcessorRegistry;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.alvexcore.repo.wfscheduler.model.WfSchedulerModel;

import java.io.Serializable;
import java.util.*;

public class WfSchudulerServiceImpl implements WfSchedulerService {
    private static Logger logger = Logger.getLogger(WfSchudulerServiceImpl.class);

    private NodeService nodeService;
    private PersonService personService;
    private ContentService contentService;
    private FormProcessorRegistry processorRegistry;
    private TransactionService transactionService;

    private static String CONTAINER_UUID = "wfscheduler-service-folder";
    private static NodeRef REF_CONTAINER = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, CONTAINER_UUID);
    private static String PREFIX_CRON_INFO = "cron_";
    private static final String[] EXCLUDE_FIELD = {"cron", "user", "workflowId"};

    private final class CronIndexes {
        public static final int MINS = 0;
        public static final int HOURS = 1;
        public static final int DAY = 2;
        public static final int MONTH = 3;
        public static final int DAY_OF_WEEK = 4;
        public static final int REPEAT = 5;
    }


    public void writeScheduleInfo(String user, JSONObject formData) {
        final NodeRef cronInfo = getCronRefByLogin(user);

        ContentReader reader = contentService.getReader(cronInfo, WfSchedulerModel.PROP_WORKFLOW_DATA);
        JSONArray json = null;
        if(reader != null){
            String jsonString = reader.getContentString();
            json = getJsonArray(jsonString);
        } else {
            json = new JSONArray();
        }

        json.add(formData);
        String updatedJson = json.toJSONString();
        ContentWriter writer = contentService.getWriter(cronInfo, WfSchedulerModel.PROP_WORKFLOW_DATA, true);
        writer.putContent(updatedJson);
    }

    public void updateAllScheduleInfo(String user, JSONArray wfData){
        final NodeRef cronInfo = getCronRefByLogin(user);
        String updatedJson = wfData.toJSONString();
        ContentWriter writer = contentService.getWriter(cronInfo, WfSchedulerModel.PROP_WORKFLOW_DATA, true);
        writer.putContent(updatedJson);
    }

    public JSONArray readAllRecordByUser(String user) {
        NodeRef cronInfo = getCronRefByLogin(user);
        if(cronInfo == null){
            return null;
        }
        ContentReader reader = contentService.getReader(cronInfo, WfSchedulerModel.PROP_WORKFLOW_DATA);
        if(reader == null){
            return null;
        }
        String jsonString = reader.getContentString();
        JSONArray json = getJsonArray(jsonString);
        return json;
    }

    private NodeRef getCronRefByLogin(String login){
        final NodeRef ref = personService.getPerson(login);
        if(ref == null){
            return null;
        }
        List<AssociationRef> assocRefs = nodeService.getSourceAssocs(ref, WfSchedulerModel.ASSOC_OWNER);
        if(assocRefs == null || assocRefs.isEmpty() || assocRefs.get(0).getSourceRef() == null){
            return createNewNode(ref);
        }

        return assocRefs.get(0).getSourceRef();
    }

    private NodeRef createNewNode(NodeRef userRef){
        Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
        String name = PREFIX_CRON_INFO + userRef.getId();
        NodeRef cronInfoRef = nodeService.createNode(getContainer(),
                ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, name),
                WfSchedulerModel.TYPE_CRON,
                props).getChildRef();
        nodeService.setAssociations(cronInfoRef, WfSchedulerModel.ASSOC_OWNER, new ArrayList<NodeRef>(Arrays.asList(userRef)));
        return cronInfoRef;
    }

    private JSONArray getJsonArray(String jsonString){
        JSONArray json = null;
        if(jsonString == null || jsonString.isEmpty()){
            json = new JSONArray();
        } else {
            JSONParser parser = new JSONParser();
            try {
                json = (JSONArray) parser.parse(jsonString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    private NodeRef getContainer(){
        if(!nodeService.exists(REF_CONTAINER)){
            throw new AlfrescoRuntimeException("Didn't find service folder. Please reimport bootstrap data");
        }
        return REF_CONTAINER;
    }

    public void startWfByCurrentDate(final Calendar cal){
        List<ChildAssociationRef> children = nodeService.getChildAssocs(getContainer());
        if(!children.isEmpty()){
            for(ChildAssociationRef child: children){
                try {
                    final NodeRef ref = child.getChildRef();
                    RetryingTransactionHelper trn = transactionService.getRetryingTransactionHelper();
                    RetryingTransactionHelper.RetryingTransactionCallback<Boolean> processTranCB = new RetryingTransactionHelper.RetryingTransactionCallback<Boolean>() {
                        public Boolean execute() throws Throwable {
                            ContentReader reader = contentService.getReader(ref, WfSchedulerModel.PROP_WORKFLOW_DATA);
                            logger.info("parse cron data with nodeRef:" + ref);
                            JSONParser parser = new JSONParser();
                            final JSONArray jsonForStart = (JSONArray) parser.parse(reader.getContentString());
                            JSONArray remainingWf = startSuitableWorkflow(jsonForStart, cal);
                            String remainingWfStr = remainingWf.toJSONString();
                            ContentWriter writer = contentService.getWriter(ref, WfSchedulerModel.PROP_WORKFLOW_DATA, true);
                            writer.putContent(remainingWfStr);
                            logger.info("save remained wf");
                            return true;
                        }
                    };
                    trn.doInTransaction(processTranCB);
                } catch (ContentIOException e){
                    logger.error("content cronInfo for node:" + child.getChildRef() + " not found");
                }
            }
        }
    }

    private JSONArray startSuitableWorkflow(JSONArray jsonWf, Calendar cal){
            JSONArray remainingWf = new JSONArray();
            for (int i=0; i<jsonWf.size(); i++){
                JSONObject data =(JSONObject) jsonWf.get(i);
                String exp = data.get("cron").toString();
                Boolean started = false;
                if(isSatisfiedBy(cal, exp)){
                    started = startWorkflow(data);
                    logger.info("workflow started: " + started);
                }
                if(isRepeat(exp) || !started){
                    remainingWf.add(data);
                    logger.info("add wf to repeat");
                }
            }

            return remainingWf;
    }

    private Boolean startWorkflow(JSONObject wfData){
        Boolean result = false;
        String login = (String) wfData.get("user");
        JSONObject workflowId = (JSONObject) wfData.get("workflowId");
        final Item item = new Item(workflowId.get("itemKind").toString(), workflowId.get("itemId").toString());
        final FormData formData = getFormData(wfData);
        result = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Boolean>()
        {
            public Boolean doWork() throws Exception
            {
                FormProcessor fp = processorRegistry.getApplicableFormProcessor(item);
                fp.persist(item, formData);
                return true;
            }
        }, login);
        return result;
    }

    private Boolean isRepeat(String exp){
        Boolean repeat = false;
        String [] expAr = exp.split(" ");
        repeat = Boolean.parseBoolean(expAr[CronIndexes.REPEAT]);
        return repeat;
    }

    /**
    *Seconds, minutes, year are ignored
    * */
    private Boolean isSatisfiedBy(Calendar cal, String cron){
        String[] cronAr = cron.split(" ");
        Integer hour = cal.get(Calendar.HOUR_OF_DAY);
        if(!equalsOrUnevirsal(hour.toString(), cronAr[CronIndexes.HOURS])){
            return false;
        }
        Integer day = cal.get(Calendar.DAY_OF_MONTH);
        if(!equalsOrUnevirsal(day.toString(), cronAr[CronIndexes.DAY])){
            return false;
        }
        Integer month = cal.get(Calendar.MONTH);
        if(!equalsOrUnevirsal(month.toString(), cronAr[CronIndexes.MONTH])){
            return false;
        }
        Integer dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(!equalsOrUnevirsal(dayOfWeek.toString(), cronAr[CronIndexes.DAY_OF_WEEK])){
            return false;
        }
        return true;
    }

    private Boolean equalsOrUnevirsal(String current, String val){
        if(current.equals(val) || val.equals("*")){
            return true;
        }
        return false;
    }


    private static FormData getFormData(JSONObject object) {
        FormData romData = new FormData();
        Set<String> keysSet = object.keySet();
        Iterator keysItr = keysSet.iterator();
        while(keysItr.hasNext()) {
            String key = keysItr.next().toString();
            Object value = object.get(key);
            if(ArrayUtils.indexOf(EXCLUDE_FIELD, key) != -1){
                continue;
            }
            romData.addFieldData(key, value, true);
        }
        return romData;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public ContentService getContentService() {
        return contentService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public FormProcessorRegistry getProcessorRegistry() {
        return processorRegistry;
    }

    public void setProcessorRegistry(FormProcessorRegistry processorRegistry) {
        this.processorRegistry = processorRegistry;
    }
    public TransactionService getTransactionService() {
        return transactionService;
    }

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

}
