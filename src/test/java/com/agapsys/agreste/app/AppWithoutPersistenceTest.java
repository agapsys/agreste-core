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
package com.agapsys.agreste.app;

import com.agapsys.agreste.AgresteApplication;
import com.agapsys.agreste.AgresteController;
import com.agapsys.agreste.test.AgresteContainer;
import com.agapsys.agreste.test.MockedWebApplication;
import com.agapsys.agreste.test.TestUtils.Endpoint;
import com.agapsys.http.HttpResponse.StringResponse;
import com.agapsys.rcf.ActionRequest;
import com.agapsys.rcf.HttpMethod;
import com.agapsys.rcf.WebAction;
import com.agapsys.rcf.WebController;
import com.agapsys.web.toolkit.modules.PersistenceModule;
import org.junit.Assert;
import org.junit.Test;

public class AppWithoutPersistenceTest {
    
    public static class NormalApplication extends MockedWebApplication {

        @Override
        public String getName() {
            return "NORMAL-APPLICATION";
        }
        
    }
    
    public static class DisabledPersistenceApplication extends MockedWebApplication {

        @Override
        public String getName() {
            return "DISABLED-PERSISTENCE-APPLICATION";
        }
        
        
        
        @Override
        protected PersistenceModule getPersistenceModule() {
            return null; // <-- Disables persistence module
        }
        
    }
    
    @WebController("controller")
    public static class MyController extends AgresteController {
        
        @WebAction(mapping = "/")
        public boolean onGet(ActionRequest request) {
            return getJpaTransaction(request) != null;
        }

    }
    

    private void _test(Class<? extends AgresteApplication> applicationClass, String expectedResponse) {
        AgresteContainer ac;

        ac = new AgresteContainer<>(applicationClass)
            .registerController(MyController.class);

        ac.start();
        
        Endpoint endpoint = new Endpoint(HttpMethod.GET, "/controller/");
        StringResponse response = ac.doRequest(endpoint.getRequest());
        Assert.assertEquals(expectedResponse, response.getContentString());
        
        ac.stop();
    }
    
    @Test
    public void test() {

        // Normal application 
        _test(NormalApplication.class, "true");
        _test(DisabledPersistenceApplication.class, "false");
    }
}
