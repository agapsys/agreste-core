/*
 * Copyright 2016-2017 Agapsys Tecnologia Ltda-ME.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.agapsys.agreste;

import com.agapsys.rcf.ActionRequest;
import com.agapsys.rcf.ActionResponse;
import com.agapsys.rcf.Controller;
import com.agapsys.rcf.User;
import com.agapsys.rcf.exceptions.ClientException;
import com.agapsys.web.toolkit.AbstractApplication;
import com.agapsys.web.toolkit.LogType;
import com.agapsys.web.toolkit.Service;
import com.agapsys.web.toolkit.services.ExceptionReporterService;
import java.io.IOException;
import java.util.NoSuchElementException;
import javax.servlet.ServletException;

public abstract class AgresteController extends Controller {

    /**
     * Returns application running instance
     * 
     * @return application running instance or null it application is not running.
     */
    public AbstractApplication getApplication() {
        synchronized (this) {
            return AbstractApplication.getRunningInstance();
        }
    }
   
    /** See {@linkplain AbstractApplication#getService(java.lang.Class, boolean)}. */
    public <S extends Service> S getService(Class<S> serviceClass, boolean autoRegistration) {
        synchronized (this) {
            AbstractApplication app = getApplication();
            
            if (app == null)
                throw new IllegalStateException("Application is not running");
            
            return app.getService(serviceClass, autoRegistration);
        }
    }

    public final <S extends Service> S getRegisteredService(Class<S> serviceClass) throws NoSuchElementException {
        S service = getService(serviceClass, false);
        
        if (service == null)
            throw new NoSuchElementException(serviceClass.getName());
        
        return service;
    }
    
    public final <S extends Service> S getOnDemandService(Class<S> serviceClass) {
        return getService(serviceClass, true);
    }
    
    /**
     * Returns the JPA transaction associated with given request.
     * 
     * NOTE: If persistence module is disabled, this method will return null.
     *
     * @param request action request.
     * @return the JPA transaction associated with given request.
     */
    public JpaTransaction getJpaTransaction(ActionRequest request) {
        return (JpaTransaction) request.getMetadata(JpaTransactionFilter.JPA_TRANSACTION_ATTRIBUTE);
    }

    @Override
    protected void onClientError(ActionRequest request, ActionResponse response, ClientException error) throws ServletException, IOException {
        __logRequest(request, LogType.WARNING, error.getMessage(), "");
        
        super.onClientError(request, response, error);
    }

    @Override
    protected boolean onUncaughtError(ActionRequest request, ActionResponse response, RuntimeException uncaughtError) throws ServletException, IOException {
        __logRequest(request, LogType.ERROR, "Application error", String.format("Stack trace:\n%s", ExceptionReporterService.getStackTrace(uncaughtError)));
        
        return super.onUncaughtError(request, response, uncaughtError);
    }
    
    private void __logRequest(ActionRequest request, LogType logType, String title, String bottomMessage) {
        getApplication().log(logType, __getLogMessage(request, title, bottomMessage));
    }
    
    private String __getLogMessage(ActionRequest request, String title, String bottomMessage) {
        User loggedUser;
        
        try {
            loggedUser = getUser(request);
        } catch (Throwable t) {
            loggedUser = null;
        }

        String requestUrl = request.getFullRequestUrl();

        StringBuilder sb = new StringBuilder(title).append('\n')
            .append("----").append('\n')
            .append(request.getMethod().name()).append(" ").append(requestUrl).append('\n')
            .append("IP: ").append(request.getOriginIp()).append('\n')
            .append("User-agent: ").append(request.getUserAgent()).append('\n')
            .append("User ID: ").append(loggedUser != null ? "" + loggedUser.toString() : "none").append('\n')
            .append(bottomMessage).append('\n')
            .append("----").append('\n');
        

        return sb.toString();

    }

}
