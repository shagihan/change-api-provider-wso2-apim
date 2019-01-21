package org.wso2.sample.changeAPIProvider.internal;

import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.user.core.service.RealmService;

public class ServiceReferenceHolder {
    private static final ServiceReferenceHolder instance = new ServiceReferenceHolder();
    private RegistryService registryService;
    private RealmService realmService;

    public RegistryService getRegistryService() {
        return registryService;
    }

    public void setRegistryService(RegistryService registryService) {
        this.registryService = registryService;
    }

    public static ServiceReferenceHolder getInstance() {
        return instance;
    }

    public RealmService getRealmService() {
        return realmService;
    }

    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

}
