/*
 * Copyright 2017 Agapsys Tecnologia Ltda-ME.
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

import com.agapsys.rcf.ActionDispatcher;
import com.agapsys.rcf.ActionRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AgresteRequest extends ActionRequest {

    public AgresteRequest(ActionRequest wrappedRequest) {
        super(wrappedRequest);
    }

    public AgresteRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        super(servletRequest, servletResponse);
    }
    
    public JpaTransaction getJpaTransaction() {
        return (JpaTransaction) getMetadata(JpaTransactionFilter.JPA_TRANSACTION_ATTRIBUTE);
    }
    
    public String getContextName() {
        String contextPath = getServletRequest().getContextPath();
        return contextPath.isEmpty() ? "/" : contextPath;
    }
    
    public String getControllerPath() {
        return ActionDispatcher.getRelativePath(getContextName(), getServletRequest().getServletPath());
    }
    
    public String getActionPath() {
        String relativePath = ActionDispatcher.getRelativePath(getControllerPath(), getRequestUri());
        
        if (relativePath.equals("/"))
            return relativePath;
        
        String[] tokens = relativePath.split("/");
        String result = "/" + tokens[1];
        
        if (getPathInfo().startsWith(result))
            return "/";
        
        return result;
    }
    
}
