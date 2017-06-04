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

import com.agapsys.agreste.test.AgresteContainer;
import com.agapsys.agreste.test.MockedAgresteApplication;
import com.agapsys.agreste.test.TestUtils;
import com.agapsys.http.HttpDelete;
import com.agapsys.http.HttpGet;
import com.agapsys.http.HttpResponse.StringResponse;
import com.agapsys.rcf.HttpMethod;
import com.agapsys.rcf.WebAction;
import com.agapsys.rcf.WebController;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author leandro-agapsys
 */
public class AgresteRequestTest {
    @WebController("myController")
    public static class MyController extends AgresteController {
        
        @WebAction(mapping = "/myAction/*")
        public String myAction(AgresteRequest request) {
            return String.format("%s, %s, %s", request.getContextName(), request.getControllerPath(), request.getActionPath());
        }
    }
    
    @WebController("root")
    public static class MyRootController extends AgresteController {
        @WebAction(mapping = "/")
        public String myRootAction1(AgresteRequest request) {
            return String.format("%s, %s, %s", request.getContextName(), request.getControllerPath(), request.getActionPath());
        }
        
        @WebAction(mapping = "/*", httpMethods = HttpMethod.DELETE)
        public String myRootAction2(AgresteRequest request) {
            return String.format("%s, %s, %s", request.getContextName(), request.getControllerPath(), request.getActionPath());
        }
    }
    
    @Test
    public void test() {
        AgresteContainer ac = new AgresteContainer<>(MockedAgresteApplication.class)
            .registerController(MyController.class)
            .registerController(MyRootController.class);
        ac.start();
        
        StringResponse resp;
        
        resp = ac.doRequest(new HttpGet("/myController/myAction/test?id=1"));
        TestUtils.assertStatus(200, resp);
        Assert.assertEquals("\"/, /myController, /myAction\"", resp.getContentString());
        
        resp = ac.doRequest(new HttpGet("/root/?id=1"));
        TestUtils.assertStatus(200, resp);
        Assert.assertEquals("\"/, /root, /\"", resp.getContentString());
        
        resp = ac.doRequest(new HttpDelete("/root/?id=1"));
        TestUtils.assertStatus(200, resp);
        Assert.assertEquals("\"/, /root, /\"", resp.getContentString());
        
        resp = ac.doRequest(new HttpDelete("/root/some/action?id=1"));
        TestUtils.assertStatus(200, resp);
        Assert.assertEquals("\"/, /root, /\"", resp.getContentString());
        
        ac.stop();
    }
}
