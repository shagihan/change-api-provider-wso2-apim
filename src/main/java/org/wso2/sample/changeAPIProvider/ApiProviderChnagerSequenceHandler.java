package org.wso2.sample.changeAPIProvider;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.wso2.carbon.apimgt.impl.utils.APIMgtDBUtil;
import org.wso2.carbon.registry.core.Collection;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.sample.changeAPIProvider.internal.ServiceReferenceHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;


public class ApiProviderChnagerSequenceHandler {

    private OMElement configFileContent;

    public void openSynapseConfigFileAndUpdateContent(String filePath, String newProvideseSynapseContent)
            throws IOException {
        File file = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (IOException e) {
            throw new IOException("Unable to read the file " + filePath, e);
        }
        //Read Content From File.
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String str = new String(data, "UTF-8");
        StringReader inStringReader = new StringReader(str);
        configFileContent = OMXMLBuilderFactory.createOMBuilder(inStringReader).getDocumentElement();
        // Update provider in config file.
        ((OMAttribute) configFileContent.getAllAttributes().next()).setAttributeValue(newProvideseSynapseContent);
        // Write Updated File.
        FileOutputStream outputStream = new FileOutputStream(filePath);
        str = configFileContent.toString();
        byte[] strToBytes = str.getBytes();
        outputStream.write(strToBytes);
        outputStream.close();
    }

    public void moveConfigFiles(String currentFile, String newFileName) throws IOException {
        File oldFile = new File(currentFile);
        File newFile = new File(newFileName);
        if (!oldFile.exists()) {
            throw new java.io.IOException("file not exists");
        }
        if (newFile.exists()) {
            throw new java.io.IOException("file already exists");
        } else {
            if (oldFile.renameTo(newFile)) {
            } else {
                throw new java.io.IOException("Unable to rename the file.");
            }
        }
    }

    public void createNewRegResourceAndMoveOldConfigs(String currentResourcePath, String newPath, String apiName,
                                                      String version, int tenantId) throws RegistryException {
        Registry registry = ServiceReferenceHolder.getInstance().getRegistryService()
                .getGovernanceSystemRegistry(tenantId);
        if (!registry.resourceExists(currentResourcePath)) {
            throw new RegistryException("Unable to find requested resource.");
        }
        if (!registry.resourceExists(newPath)) {
            Collection newCollection = registry.newCollection();
            registry.put(newPath, newCollection);
        }
        String newNamedCollection = newPath + Constants.FORWARD_SLASH + apiName;
        if (!registry.resourceExists(newNamedCollection)) {
            Collection newCollection = registry.newCollection();
            registry.put(newNamedCollection, newCollection);
        }
        registry.move(currentResourcePath, newNamedCollection + Constants.FORWARD_SLASH + version);
    }

    public void updateRegisteryEntries(String newRegPath, String apiName, String apiVersion,
                                       String newApiProviderRegValue, int tenantId)
            throws RegistryException {
        Registry registry = ServiceReferenceHolder.getInstance().getRegistryService()
                .getGovernanceSystemRegistry(tenantId);
        Resource resource = registry.get(newRegPath + Constants.FORWARD_SLASH + apiName + Constants.FORWARD_SLASH
                + apiVersion + Constants.FORWARD_SLASH + Constants.API_VAR);
        //Put updated api resource.
        byte[] content = (byte[]) resource.getContent();
        String stringContent = new String(content);
        StringReader inStringReader = new StringReader(stringContent);
        OMElement omElement = OMXMLBuilderFactory.createOMBuilder(inStringReader).getDocumentElement();
        Iterator childElements = omElement.getFirstElement().getChildElements();
        while (childElements.hasNext()) {
            OMElement getQuote = (OMElement) childElements.next();
            String localName = getQuote.getLocalName();
            if (localName.equals(Constants.PROVIDER)) {
                getQuote.setText(newApiProviderRegValue);
                break;
            }
        }

        resource.setContent(omElement.toString());
        //Delete old api resource
        registry.delete(newRegPath + Constants.FORWARD_SLASH + apiName + Constants.FORWARD_SLASH + apiVersion
                + Constants.FORWARD_SLASH + Constants.API_VAR);
        registry.put(newRegPath + Constants.FORWARD_SLASH + apiName + Constants.FORWARD_SLASH + apiVersion
                + Constants.FORWARD_SLASH + Constants.API_VAR, resource);
    }

    public void updateDatabaseEntries(String apiName, String apiVersion, String newProviderDbEntry,
                                      String currentProviderDbEntry, String newProvider, String userStoreDomain) throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sqlQuery = "SELECT * FROM AM_API WHERE API_NAME = ? AND API_VERSION=? AND API_PROVIDER = ?";
        try {
            conn = APIMgtDBUtil.getConnection();
            conn.setAutoCommit(false);
            preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setString(1, apiName);
            preparedStatement.setString(2, apiVersion);
            preparedStatement.setString(3, currentProviderDbEntry);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                sqlQuery = "UPDATE AM_API SET API_PROVIDER = ? WHERE API_NAME = ? " +
                        "AND API_VERSION=? AND API_PROVIDER = ?";
                preparedStatement = conn.prepareStatement(sqlQuery);
                preparedStatement.setString(1, newProviderDbEntry);
                preparedStatement.setString(2, apiName);
                preparedStatement.setString(3, apiVersion);
                preparedStatement.setString(4, currentProviderDbEntry);
                preparedStatement.executeUpdate();
                sqlQuery = "UPDATE AM_API_LC_EVENT SET USER_ID  = ? WHERE API_ID = (SELECT API_ID FROM AM_API " +
                        "WHERE API_NAME = ? AND API_VERSION=? AND API_PROVIDER = ?)";
                preparedStatement = conn.prepareStatement(sqlQuery);
                if (userStoreDomain.equals(Constants.EMPTY_STRING)) {
                    preparedStatement.setString(1, newProvider);
                } else {
                    preparedStatement.setString(1, userStoreDomain.toUpperCase()
                            + Constants.FORWARD_SLASH + newProvider);
                }
                preparedStatement.setString(2, apiName);
                preparedStatement.setString(3, apiVersion);
                preparedStatement.setString(4, newProviderDbEntry);
                preparedStatement.executeUpdate();
                conn.commit();
            }
        } catch (SQLException e) {
            conn.rollback();
            throw new SQLException("Unable to update database", e);
        } finally {
            conn.commit();
            APIMgtDBUtil.closeAllConnections(preparedStatement, conn, resultSet);
        }
    }
}
