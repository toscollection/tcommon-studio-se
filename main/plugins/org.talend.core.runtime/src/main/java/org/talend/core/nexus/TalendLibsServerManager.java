// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.core.nexus;

import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.properties.User;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.core.runtime.i18n.Messages;
import org.talend.core.runtime.projectsetting.ProjectPreferenceManager;
import org.talend.core.service.IRemoteService;
import org.talend.core.utils.SecurityStorageUtil;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryConstants;

/**
 * created by wchen on 2015年6月16日 Detailled comment
 *
 */
public class TalendLibsServerManager {

    private ProjectPreferenceManager prefManager;

    public static final String KEY_LIB_REPO_URL = "org.talend.libraries.repo.url";

    private static String NEXUS_USER = "nexus.user";

    private static String NEXUS_PASSWORD = "nexus.password";

    private static String NEXUS_URL = "nexus.url";

    private static String NEXUS_LIB_REPO = "nexus.lib.repo";

    private static String DEFAULT_LIB_REPO = "talend-custom-libs-release";

    private static String NEXUS_LIB_SNAPSHOT_REPO = "nexus.lib.repo.snapshot";

    private static String DEFAULT_LIB_SNAPSHOT_REPO = "talend-custom-libs-snapshot";

    private static String NEXUS_LIB_SERVER_TYPE = "nexus.lib.server.type";

    public static final String KEY_NEXUS_RUL = "url";//$NON-NLS-1$

    public static final String KEY_NEXUS_USER = "username";//$NON-NLS-1$

    public static final String KEY_NEXUS_PASS = "password";//$NON-NLS-1$

    public static final String KEY_CUSTOM_LIB_REPOSITORY = "repositoryReleases";//$NON-NLS-1$

    public static final String KEY_CUSTOM_LIB_SNAPSHOT_REPOSITORY = "repositorySnapshots";//$NON-NLS-1$

    public static final String KEY_SOFTWARE_UPDATE_REPOSITORY = "repositoryID";//$NON-NLS-1$

    public static final String TALEND_LIB_SERVER = "https://talend-update.talend.com/nexus/";//$NON-NLS-1$

    public static final String NEXUS_PROXY_URL = "nexus.proxy.url";

    public static final String NEXUS_PROXY_TYPE = "nexus.proxy.type";

    public static final String NEXUS_PROXY_USERNAME = "nexus.proxy.username";

    public static final String NEXUS_PROXY_PASSWORD = "nexus.proxy.password";

    public static final String NEXUS_PROXY_REPOSITORY_ID = "nexus.proxy.repository.id";

    public static final String ENABLE_PROXY_SETTING = "nexus.proxy.enable";

    public static final String NEXUS_PROXY_STORAGE_CATEGORY = "org.talend.artifact.proxy.setting";

    public static final String TALEND_LIB_USER = "";//$NON-NLS-1$

    public static final String TALEND_LIB_PASSWORD = "";//$NON-NLS-1$

    public static final String TALEND_LIB_REPOSITORY = "studio-libraries";//$NON-NLS-1$

    private static TalendLibsServerManager manager = null;

    private ArtifactRepositoryBean artifactServerBean;

    private long artifactLastTimeInMillis = 0;

    private ArtifactRepositoryBean softWareUpdateServerBean;

    private long softWareLastTimeInMillis = 0;

    private int timeGap = 30 * 60 * 1000;

    public static synchronized TalendLibsServerManager getInstance() {
        if (manager == null) {
            manager = new TalendLibsServerManager();
        }
        return manager;
    }

    public ArtifactRepositoryBean getCustomNexusServer() {
        if (!org.talend.core.PluginChecker.isCoreTISPluginLoaded()) {
            return null;
        }
        Date date = new Date();
        // avoid to connect to tac too many times
        if (artifactServerBean == null && date.getTime() - artifactLastTimeInMillis > timeGap) {
            try {
                artifactLastTimeInMillis = date.getTime();
                String nexus_url = System.getProperty(NEXUS_URL);
                String nexus_user = System.getProperty(NEXUS_USER);
                String nexus_pass = System.getProperty(NEXUS_PASSWORD);
                String repositoryId = System.getProperty(NEXUS_LIB_REPO, DEFAULT_LIB_REPO);
                String snapshotRepId = System.getProperty(NEXUS_LIB_SNAPSHOT_REPO, DEFAULT_LIB_SNAPSHOT_REPO);
                String serverType = System.getProperty(NEXUS_LIB_SERVER_TYPE, "NEXUS_2");

                IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
                RepositoryContext repositoryContext = factory.getRepositoryContext();
                if ((nexus_url == null && (factory.isLocalConnectionProvider() || repositoryContext.isOffline()))) {
                    return null;
                }
                if (repositoryContext != null && repositoryContext.getFields() != null && !factory.isLocalConnectionProvider()
                        && !repositoryContext.isOffline()) {
                    String adminUrl = repositoryContext.getFields().get(RepositoryConstants.REPOSITORY_URL);
                    String userName = "";
                    String password = "";
                    User user = repositoryContext.getUser();
                    if (user != null) {
                        userName = user.getLogin();
                        password = repositoryContext.getClearPassword();
                    }

                    if (adminUrl != null && !"".equals(adminUrl)
                            && GlobalServiceRegister.getDefault().isServiceRegistered(IRemoteService.class)) {
                        IRemoteService remoteService = (IRemoteService) GlobalServiceRegister.getDefault()
                                .getService(IRemoteService.class);
                        ArtifactRepositoryBean bean = remoteService.getLibNexusServer(userName, password, adminUrl);
                        if (bean != null) {
                            serverType = bean.getType();
                            nexus_url = bean.getServer();
                            nexus_user = bean.getUserName();
                            nexus_pass = bean.getPassword();
                            repositoryId = bean.getRepositoryId();
                            snapshotRepId = bean.getSnapshotRepId();
                            System.setProperty(NEXUS_URL, nexus_url);
                            System.setProperty(NEXUS_USER, nexus_user);
                            System.setProperty(NEXUS_PASSWORD, nexus_pass);
                            System.setProperty(NEXUS_LIB_REPO, repositoryId);
                            System.setProperty(NEXUS_LIB_SNAPSHOT_REPO, snapshotRepId);
                        }
                    }
                }
                if (nexus_url == null) {
                    return null;
                }
                ArtifactRepositoryBean serverBean = new ArtifactRepositoryBean();
                serverBean.setServer(nexus_url);
                serverBean.setUserName(nexus_user);
                serverBean.setPassword(nexus_pass);
                serverBean.setRepositoryId(repositoryId);
                serverBean.setSnapshotRepId(snapshotRepId);
                serverBean.setType(serverType);

                IRepositoryArtifactHandler repHander = RepositoryArtifactHandlerManager.getRepositoryHandler(serverBean);
                if (repHander.checkConnection()) {
                    artifactServerBean = serverBean;
                }

            } catch (Exception e) {
                artifactServerBean = null;
                ExceptionHandler.process(e);
            }
        }
        return artifactServerBean;

    }

    /**
     * 
     * Check user library connection with the setting from remote administrator
     * 
     * @return
     * @throws PersistenceException
     */
    public boolean canConnectUserLibrary() throws PersistenceException {
        boolean canConnect = false;
        IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();
        RepositoryContext repositoryContext = factory.getRepositoryContext();
        ArtifactRepositoryBean bean = null;
        try {
            if (repositoryContext != null && repositoryContext.getFields() != null && !factory.isLocalConnectionProvider()
                    && !repositoryContext.isOffline()) {
                String adminUrl = repositoryContext.getFields().get(RepositoryConstants.REPOSITORY_URL);
                String userName = null;
                String password = null;
                User user = repositoryContext.getUser();
                if (user != null) {
                    userName = user.getLogin();
                    password = repositoryContext.getClearPassword();
                }

                if (StringUtils.isNotBlank(adminUrl) && StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)
                        && GlobalServiceRegister.getDefault().isServiceRegistered(IRemoteService.class)) {
                    IRemoteService remoteService = (IRemoteService) GlobalServiceRegister.getDefault()
                            .getService(IRemoteService.class);
                    bean = remoteService.getLibNexusServer(userName, password, adminUrl);
                    if (bean != null) {
                        IRepositoryArtifactHandler handler = RepositoryArtifactHandlerManager.getRepositoryHandler(bean);
                        if (handler.checkConnection()) {
                            canConnect = true;
                        } else {
                            ExceptionHandler.process(new Throwable(Messages.getString(
                                    "TalendLibsServerManager.connectUserLibraryFailureMessage", bean.getServer()))); //$NON-NLS-1$
                        }
                    }
                }
            }
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
        if (bean == null) {
            throw new PersistenceException(Messages.getString("TalendLibsServerManager.cannotGetUserLibraryServer"));
        }
        return canConnect;
    }

    public boolean isProxyArtifactRepoConfigured() {
        ArtifactRepositoryBean serverBean = getProxyArtifactServer();
        return serverBean == null ? false : true;
    }

    public ArtifactRepositoryBean getProxyArtifactServer() {
        ArtifactRepositoryBean serverBean = new ArtifactRepositoryBean();
        // get from ini file first
        String url = System.getProperty(NEXUS_PROXY_URL);
        if (StringUtils.isNotEmpty(url)) {
            serverBean.setServer(System.getProperty(NEXUS_PROXY_URL));
            serverBean.setUserName(System.getProperty(NEXUS_PROXY_USERNAME));
            serverBean.setPassword(System.getProperty(NEXUS_PROXY_PASSWORD));
            serverBean.setRepositoryId(System.getProperty(NEXUS_PROXY_REPOSITORY_ID));
            serverBean.setType(System.getProperty(NEXUS_PROXY_TYPE));
        }
        // if not set in ini file ,get from project setting
        boolean hasProxySetting = StringUtils.isNotEmpty(serverBean.getServer());
        if (!hasProxySetting) {
            prefManager = new ProjectPreferenceManager("org.talend.proxy.nexus", true);
            boolean enableProxyFlag = prefManager.getBoolean(TalendLibsServerManager.ENABLE_PROXY_SETTING);
            if (enableProxyFlag) {
                serverBean.setServer(prefManager.getValue(TalendLibsServerManager.NEXUS_PROXY_URL));
                serverBean.setRepositoryId(prefManager.getValue(TalendLibsServerManager.NEXUS_PROXY_REPOSITORY_ID));
                serverBean.setType(prefManager.getValue(TalendLibsServerManager.NEXUS_PROXY_TYPE));
                String[] credentials = getProxyArtifactCredentials(serverBean.getServer(), serverBean.getRepositoryId(),
                        NEXUS_PROXY_USERNAME, NEXUS_PROXY_PASSWORD);
                if (credentials != null) {
                    serverBean.setUserName(credentials[0]);
                    serverBean.setPassword(credentials[1]);
                }
            }
        }
        if (StringUtils.isNotEmpty(serverBean.getServer())) {
            return serverBean;
        }
        return null;
    }

    public String[] getProxyArtifactCredentials(String url, String repositoryId, String usernameKey, String passwordKey) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            String path = getStoragePath(url, repositoryId);
            Map<String, String> storageNodePairs = SecurityStorageUtil.getSecurityStorageNodePairs(path);
            if (storageNodePairs != null) {
                String username = storageNodePairs.get(usernameKey);
                String password = storageNodePairs.get(passwordKey);
                if (username == null && password == null) {
                    return null;
                }
                return new String[] { username, password };
            }
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
        return null;
    }

    public void saveProxyArtifactCredentials(String url, String repositoryId, String usernameKey, String username,
            String passwordKey, String password) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        try {
            String path = getStoragePath(url, repositoryId);
            SecurityStorageUtil.saveToSecurityStorage(path, usernameKey, username, false, false);
            SecurityStorageUtil.saveToSecurityStorage(path, passwordKey, password, true, false);
            SecurityStorageUtil.flushSecurityStorage();
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
    }

    public void saveProxyArtifactCredentialsUserName(String url, String repositoryId, String usernameKey, String username,
            boolean flush) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        try {
            String path = getStoragePath(url, repositoryId);
            SecurityStorageUtil.saveToSecurityStorage(path, usernameKey, username, false, flush);
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
    }

    public void saveProxyArtifactCredentialsPassword(String url, String repositoryId, String passwordKey, String password,
            boolean flush) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        try {
            String path = getStoragePath(url, repositoryId);
            SecurityStorageUtil.saveToSecurityStorage(path, passwordKey, password, true, flush);
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
    }

    public void flushSecurityStorage() {
        try {
            SecurityStorageUtil.flushSecurityStorage();
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
    }

    private String getStoragePath(String url, String repositoryId) throws Exception {
        String node = url;
        if (StringUtils.isNotBlank(repositoryId)) {
            if (!url.endsWith("/")) {
                node = node + "/";
            }
            node = node + repositoryId;
        }
        node = URLEncoder.encode(node, "UTF-8");
        String path = NEXUS_PROXY_STORAGE_CATEGORY + "/" + node;
        return path;
    }

    public ArtifactRepositoryBean getTalentArtifactServer() {
        ArtifactRepositoryBean serverBean = getProxyArtifactServer();
        if (serverBean == null) {
            serverBean = new ArtifactRepositoryBean();
            serverBean.setServer(TALEND_LIB_SERVER);
            serverBean.setUserName(TALEND_LIB_USER);
            serverBean.setPassword(TALEND_LIB_PASSWORD);
            serverBean.setRepositoryId(TALEND_LIB_REPOSITORY);
        }
        serverBean.setOfficial(true);
        return serverBean;
    }

    public String resolveSha1(String nexusUrl, String userName, String password, String repositoryId, String groupId,
            String artifactId, String version, String type) throws Exception {
        return NexusServerUtils.resolveSha1(nexusUrl, userName, password, repositoryId, groupId, artifactId, version, type);
    }

    /**
     *
     * DOC Talend Comment method "getSoftwareUpdateNexusServer". get nexus server configured in TAC for patch
     *
     * @param adminUrl
     * @param userName
     * @param password
     * @return
     */
    public ArtifactRepositoryBean getSoftwareUpdateNexusServer(String adminUrl, String userName, String password) {
        try {
            Date date = new Date();
            if (softWareUpdateServerBean == null && date.getTime() - softWareLastTimeInMillis > timeGap) {
                softWareLastTimeInMillis = date.getTime();
                if (adminUrl != null && !"".equals(adminUrl)
                        && GlobalServiceRegister.getDefault().isServiceRegistered(IRemoteService.class)) {
                    IRemoteService remoteService = (IRemoteService) GlobalServiceRegister.getDefault()
                            .getService(IRemoteService.class);
                    ArtifactRepositoryBean serverBean = remoteService.getUpdateRepositoryUrl(userName, password, adminUrl);
                    IRepositoryArtifactHandler repHander = RepositoryArtifactHandlerManager.getRepositoryHandler(serverBean);
                    if (repHander.checkConnection(true, false)) {
                        softWareUpdateServerBean = serverBean;
                    }
                }
            }
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        } catch (LoginException e) {
            ExceptionHandler.process(e);
        }

        return softWareUpdateServerBean;
    }

}
