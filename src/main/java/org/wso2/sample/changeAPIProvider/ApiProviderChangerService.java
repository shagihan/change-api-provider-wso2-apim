package org.wso2.sample.changeAPIProvider;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.AbstractAdmin;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.registry.core.Collection;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.rest.api.stub.types.carbon.APIData;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.sample.changeAPIProvider.internal.ServiceReferenceHolder;

public class ApiProviderChangerService extends AbstractAdmin {
    private static final Log log = LogFactory.getLog(ApiProviderChangerService.class);
    private ApiProviderChangeUtils apiProviderChangeUtils;

    public String setNewAPIProvider(String apiNAme, String apiVersion, String currentProvider, String newProvider,
                                    String tenantDomain, String userStoreDomain) throws IOException, RegistryException, SQLException {

        int tenantId = startTenantFlow(tenantDomain);
        boolean isTeanantFolw = false;
        if (tenantId != -1234) {
            isTeanantFolw = true;
        }
        log.info("Updating provider of API : " + apiNAme);
        apiProviderChangeUtils = new ApiProviderChangeUtils(apiNAme, apiVersion, currentProvider, tenantId,
                newProvider, userStoreDomain, tenantDomain, true, isTeanantFolw);
        ApiProviderChnagerSequenceHandler apiProviderChnagerSequenceHandler = new ApiProviderChnagerSequenceHandler();

        /*Update Database*/
        apiProviderChnagerSequenceHandler.updateDatabaseEntries(apiNAme, apiVersion,
                apiProviderChangeUtils.getNewRegProvideForDB(), apiProviderChangeUtils.getCurrentRegProvideForDB(),
                newProvider, userStoreDomain);
        log.info("1. DB Resources updated");

        /*Update file content.*/
        apiProviderChnagerSequenceHandler.openSynapseConfigFileAndUpdateContent(apiProviderChangeUtils.getFilePath()
                + apiProviderChangeUtils.getCurrentFileName(), apiProviderChangeUtils.getNewSynapseConfigApiName());
        log.info("2. Synapse configs updated");

        /*Move file with new name.*/
        apiProviderChnagerSequenceHandler.moveConfigFiles(apiProviderChangeUtils.getFilePath()
                + apiProviderChangeUtils.getCurrentFileName(), apiProviderChangeUtils.getFilePath()
                + apiProviderChangeUtils.getNewFileName());
        log.info("3. Files successfully renamed");

        /*Create new Reg Resource.*/
        apiProviderChnagerSequenceHandler.createNewRegResourceAndMoveOldConfigs(apiProviderChangeUtils
                        .getCurrentRegPath(), apiProviderChangeUtils.getNewRegPathForCollection(),
                apiProviderChangeUtils.getApiName(), apiProviderChangeUtils.getApiVersion(), tenantId);
        log.info("4. Registry resources updated.");

        /*Update reg Resource.*/
        apiProviderChnagerSequenceHandler.updateRegisteryEntries(apiProviderChangeUtils.getNewRegPathForCollection(),
                apiNAme, apiVersion, apiProviderChangeUtils.getNewRegProvider(), tenantId);
        log.info("Successfully updated provider of API : " + apiNAme);
        return "Successfully changed the provider";
    }

    private int startTenantFlow(String tenantDomain) {
        // Start a new tenant flow for a tenant
        log.info("Tenant '" + tenantDomain + "' update started.");
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain);
        int tenantId = 0;
        try {
            tenantId = ServiceReferenceHolder.getInstance().getRealmService().getTenantManager()
                    .getTenantId(tenantDomain);
        } catch (UserStoreException e) {
            log.error("User store exception while getting the tenant ID for tenant " +
                    tenantDomain, e);
        }
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(tenantId);
        return tenantId;
    }

}
