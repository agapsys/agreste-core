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
import com.agapsys.web.toolkit.Module;
import com.agapsys.web.toolkit.Service;
import com.agapsys.web.toolkit.modules.ExceptionReporterModule;
import java.io.IOException;
import javax.persistence.OptimisticLockException;
import javax.servlet.ServletException;

public abstract class AgresteController extends Controller {

    // <editor-fold desc="STATIC SCOPE">
    // =========================================================================
    /**
     * Returns an application module.
     * @param <M> module type
     * @param moduleClass module class.
     * @return application module associated with given class.
     */
    public static <M extends Module> M getModule(Class<M> moduleClass) {
        return AbstractApplication.getRunningInstance().getModule(moduleClass);
    }

    /**
     * Returns an application service.
     * @param <S> service type
     * @param serviceClass service class.
     * @return application service associated with given class.
     */
    public static <S extends Service> S getService(Class<S> serviceClass) {
        return AbstractApplication.getRunningInstance().getService(serviceClass);
    }

    /**
     * Returns the JPA transaction associated with given request.
     *
     * @param request action request.
     * @return the JPA transaction associated with given request.
     */
    public static JpaTransaction getJpaTransaction(ActionRequest request) {
        return (JpaTransaction) request.getMetadata(JpaTransactionFilter.JPA_TRANSACTION_ATTRIBUTE);
    }
    // =========================================================================
    // </editor-fold>

    @Override
    protected final void onClientError(ActionRequest request, ActionResponse response, ClientException error) throws ServletException, IOException {
        logRequest(request, LogType.WARNING, error.getMessage());
        super.onClientError(request, response, error);

        onClientException(request, response, error);
    }

    protected void onClientException(ActionRequest request, ActionResponse response, ClientException exception) throws ServletException, IOException {}

    @Override
    protected final boolean onControllerError(ActionRequest request, ActionResponse response, Throwable unacaughtError) throws ServletException, IOException {

        if (!(unacaughtError instanceof OptimisticLockException)) { // <-- OptimisticLockException will be handled by JpaFilter!
            String stackTrace = ExceptionReporterModule.getStackTrace(unacaughtError);
            logRequest(request, LogType.ERROR, stackTrace);
            return true;
        } else {
            super.onControllerError(request, response, unacaughtError);
            return onUncaughtError(request, response, unacaughtError);
        }
    }

    protected boolean onUncaughtError(ActionRequest request, ActionResponse response, Throwable uncaughtError) throws ServletException, IOException {
        return true;
    }


    protected String getLogMessage(ActionRequest request, String message) throws ServletException, IOException {

        User loggedUser = getUser(request);

        String requestUrl = request.getFullRequestUrl();

        String finalMessage =  String.format("%s %s\nIP: %s\nUser-agent: %s\nUser id: %s%s",
            request.getMethod().name(),
            requestUrl,
            request.getOriginIp(),
            request.getUserAgent(),
            loggedUser != null ? "" + loggedUser.toString(): "none",
            message != null && !message.trim().isEmpty() ? "\n\n" + message : ""
        );

        return finalMessage;

    }

    public void logRequest(ActionRequest request, LogType logType, String message) throws ServletException, IOException {
        String consoleLogMessage = String.format("%s\n----\n%s\n----", message, getLogMessage(request, null));
        AbstractApplication.getRunningInstance().log(logType, consoleLogMessage);
    }

}
