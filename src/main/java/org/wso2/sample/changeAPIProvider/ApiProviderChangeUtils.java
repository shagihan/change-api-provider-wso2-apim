package org.wso2.sample.changeAPIProvider;

public class ApiProviderChangeUtils {
    private String apiName;
    private String apiVersion;
    private String currentProvider;
    private String newProvider;
    private String userStoreDomain = Constants.EMPTY_STRING;
    private String tenantDomain;
    private int tenantId;
    private boolean isPreserveUserStorDomain;
    private boolean isTenantFlow;

    public ApiProviderChangeUtils(String apiName, String apiVersion, String currentProvider, int tenantId,
                                  String newProvider, String userStoreDomain, String tenantDomain,
                                  boolean isPreserveUserStorDomain, boolean isTenantFlow) {
        this.apiName = apiName;
        this.apiVersion = apiVersion;
        this.currentProvider = currentProvider;
        this.newProvider = newProvider;
        this.userStoreDomain = userStoreDomain;
        this.tenantDomain = tenantDomain;
        this.tenantId = tenantId;
        this.isPreserveUserStorDomain = isPreserveUserStorDomain;
        this.isTenantFlow = isTenantFlow;
    }

    public String getCurrentFileName() {
        if (userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId == -1234) {
            //admin--PizzaShackAPI_v1.0.0.xml
            return currentProvider + Constants.PROVIDER_DELIM + apiName + Constants.VERSION_DELIM + apiVersion
                    + Constants.FILE_EXTENTION;
        } else if (userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId != -1234) {
            //admin-AT-abc.com--PizzaShackAPI_v1.0.0.xml
            return currentProvider + Constants.PROVIDER_SINGLE_DELIM + tenantDomain + Constants.PROVIDER_DELIM
                    + apiName + Constants.VERSION_DELIM + apiVersion + Constants.FILE_EXTENTION;
        } else if (!userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId == -1234) {
            //TEST.COM_shagihan--PizzaShackAPI_v1.0.0.xml
            return userStoreDomain.toUpperCase() + Constants.UNSERSCORE_DELIM
                    + currentProvider + Constants.PROVIDER_DELIM + apiName
                    + Constants.VERSION_DELIM + apiVersion + Constants.FILE_EXTENTION;

        } else {
            //ABCD_shagihan-AT-abc.com--PizzaShackAPI_v1.0.0.xml
            return userStoreDomain.toUpperCase() + Constants.UNSERSCORE_DELIM
                    + currentProvider + Constants.PROVIDER_SINGLE_DELIM + tenantDomain + Constants.PROVIDER_DELIM
                    + apiName + Constants.VERSION_DELIM + apiVersion + Constants.FILE_EXTENTION;
        }
    }

    public String getNewFileName() {
        if (userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId == -1234) {
            //admin--PizzaShackAPI_v1.0.0.xml
            return newProvider + Constants.PROVIDER_DELIM + apiName + Constants.VERSION_DELIM + apiVersion
                    + Constants.FILE_EXTENTION;
        } else if (userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId != -1234) {
            //admin-AT-abc.com--PizzaShackAPI_v1.0.0.xml
            return newProvider + Constants.PROVIDER_SINGLE_DELIM + tenantDomain + Constants.PROVIDER_DELIM
                    + apiName + Constants.VERSION_DELIM + apiVersion + Constants.FILE_EXTENTION;
        } else if (!userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId == -1234) {
            //TEST.COM_shagihan--PizzaShackAPI_v1.0.0.xml
            return userStoreDomain.toUpperCase() + Constants.UNSERSCORE_DELIM
                    + newProvider + Constants.PROVIDER_DELIM + apiName
                    + Constants.VERSION_DELIM + apiVersion + Constants.FILE_EXTENTION;

        } else {
            //ABCD_shagihan-AT-abc.com--PizzaShackAPI_v1.0.0.xml
            return userStoreDomain.toUpperCase() + Constants.UNSERSCORE_DELIM
                    + newProvider + Constants.PROVIDER_SINGLE_DELIM + tenantDomain + Constants.PROVIDER_DELIM
                    + apiName + Constants.VERSION_DELIM + apiVersion + Constants.FILE_EXTENTION;
        }
    }

    public String getCurrentSynapseConfigApiName() {
        if (userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId == -1234) {
            //default : admin--PizzaShackAPI
            return currentProvider + Constants.PROVIDER_DELIM + apiName;
        } else if (userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId != -1234) {
            // tenant only : admin-AT-abc.com--PizzaShackAPI
            return currentProvider + Constants.PROVIDER_SINGLE_DELIM + tenantDomain
                    + Constants.PROVIDER_DELIM + apiName;
        } else if (!userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId == -1234) {
            // User Store only : TEST.COM/shagihan--PizzaShackAPI
            return userStoreDomain + Constants.FORWARD_SLASH + currentProvider + Constants.PROVIDER_DELIM + apiName;
        } else {
            //user store+tenant : ABCD/shagihan-AT-abc.com--PizzaShackAPI
            return userStoreDomain + Constants.FORWARD_SLASH + currentProvider + Constants.PROVIDER_SINGLE_DELIM
                    + tenantDomain + Constants.PROVIDER_DELIM + apiName;
        }
    }

    public String getNewSynapseConfigApiName() {
        if (userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId == -1234) {
            //default : admin--PizzaShackAPI
            return newProvider + Constants.PROVIDER_DELIM + apiName;
        } else if (userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId != -1234) {
            // tenant only : admin-AT-abc.com--PizzaShackAPI
            return newProvider + Constants.PROVIDER_SINGLE_DELIM + tenantDomain
                    + Constants.PROVIDER_DELIM + apiName;
        } else if (!userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId == -1234) {
            // User Store only : TEST.COM/shagihan--PizzaShackAPI
            if (isPreserveUserStorDomain) {
                return userStoreDomain + Constants.FORWARD_SLASH + newProvider + Constants.PROVIDER_DELIM + apiName;
            } else {
                return newProvider + Constants.PROVIDER_DELIM + apiName;
            }
        } else {
            //user store+tenant : ABCD/shagihan-AT-abc.com--PizzaShackAPI
            if (isPreserveUserStorDomain) {
                return userStoreDomain + Constants.FORWARD_SLASH + newProvider + Constants.PROVIDER_SINGLE_DELIM
                        + tenantDomain + Constants.PROVIDER_DELIM + apiName;
            } else {
                return newProvider + Constants.PROVIDER_SINGLE_DELIM + tenantDomain
                        + Constants.PROVIDER_DELIM + apiName;
            }
        }
    }

    public String getCurrentRegPath() {
        if (userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId == -1234) {
            // default : /_system/governance/apimgt/applicationdata/provider/admin/PizzaShackAPI/1.0.0/api
            return Constants.REG_RESOURCE_API_PROVIDER + Constants.FORWARD_SLASH + currentProvider
                    + Constants.FORWARD_SLASH + apiName + Constants.FORWARD_SLASH + apiVersion;
        } else if (userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId != -1234) {
            // tenant only : /_system/governance/apimgt/applicationdata/provider/admin-AT-abc.com/PizzaShackAPI/1.0.0/api
            return Constants.REG_RESOURCE_API_PROVIDER + Constants.FORWARD_SLASH + currentProvider
                    + Constants.PROVIDER_SINGLE_DELIM + tenantDomain + Constants.FORWARD_SLASH + apiName
                    + Constants.FORWARD_SLASH + apiVersion;
        } else if (!userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId == -1234) {
            // user store only : /_system/governance/apimgt/applicationdata/provider/TEST.COM/shagihan/PizzaShackAPI/1.0.0/api
            return Constants.REG_RESOURCE_API_PROVIDER + Constants.FORWARD_SLASH + userStoreDomain.toUpperCase()
                    + Constants.FORWARD_SLASH + currentProvider + Constants.FORWARD_SLASH + apiName
                    + Constants.FORWARD_SLASH + apiVersion;
        } else {
            // tenant + user store : /_system/governance/apimgt/applicationdata/provider/ABCD/shagihan-AT-abc.com/PizzaShackAPI/1.0.0/api
            return Constants.REG_RESOURCE_API_PROVIDER + Constants.FORWARD_SLASH + userStoreDomain.toUpperCase()
                    + Constants.FORWARD_SLASH + currentProvider + Constants.PROVIDER_SINGLE_DELIM + tenantDomain
                    + Constants.FORWARD_SLASH + apiName + Constants.FORWARD_SLASH + apiVersion;
        }
    }

    public String getNewRegPathForCollection() {
        if (userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId == -1234) {
            // default : /_system/governance/apimgt/applicationdata/provider/admin/PizzaShackAPI
            return Constants.REG_RESOURCE_API_PROVIDER + Constants.FORWARD_SLASH + newProvider;
        } else if (userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId != -1234) {
            // tenant only : /_system/governance/apimgt/applicationdata/provider/admin-AT-abc.com
            return Constants.REG_RESOURCE_API_PROVIDER + Constants.FORWARD_SLASH + newProvider
                    + Constants.PROVIDER_SINGLE_DELIM + tenantDomain;
        } else if (!userStoreDomain.equalsIgnoreCase(Constants.EMPTY_STRING) && tenantId == -1234) {
            // user store only : /_system/governance/apimgt/applicationdata/provider/TEST.COM/shagihan
            return Constants.REG_RESOURCE_API_PROVIDER + Constants.FORWARD_SLASH + userStoreDomain.toUpperCase()
                    + Constants.FORWARD_SLASH + newProvider;
        } else {
            // tenant + user store : /_system/governance/apimgt/applicationdata/provider/ABCD/shagihan-AT-abc.com
            return Constants.REG_RESOURCE_API_PROVIDER + Constants.FORWARD_SLASH + userStoreDomain.toUpperCase()
                    + Constants.FORWARD_SLASH + newProvider + Constants.PROVIDER_SINGLE_DELIM + tenantDomain;
        }
    }

    public String getCurrentRegProvide() {
        // tenant only : admin-AT-abc.com
        // tenant + user store : ABCD/shagihan-AT-abc.com
        return getCurrentSynapseConfigApiName().split(Constants.PROVIDER_DELIM)[0];
    }

    public String getNewRegProvider() {
        // tenant only : admin-AT-abc.com
        // tenant + user store : ABCD/shagihan-AT-abc.com
        return getNewSynapseConfigApiName().split(Constants.PROVIDER_DELIM)[0];
    }

    public String getCurrentRegProvideForDB() {
        String temp = getCurrentRegProvide();
        if(temp.contains(Constants.PROVIDER_SINGLE_DELIM)) {
            return temp.replace(Constants.PROVIDER_SINGLE_DELIM,"@");
        }
        return temp;
    }

    public String getNewRegProvideForDB() {
        String temp = getNewRegProvider();
        if(temp.contains(Constants.PROVIDER_SINGLE_DELIM)) {
            return temp.replace(Constants.PROVIDER_SINGLE_DELIM,"@");
        }
        return temp;
    }

    public String getApiName() {
        return apiName;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getFilePath () {
        if (!isTenantFlow) {
            return Constants.CARBON_HOME + Constants.SUPER_TENANT_API_DEF_DIR;
        } else {
            return Constants.CARBON_HOME+Constants.TENANT_DIR+tenantId+Constants.TENANT_API_DEF_DIR;
        }
    }
}
