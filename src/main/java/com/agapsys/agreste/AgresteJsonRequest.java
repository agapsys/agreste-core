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

import com.agapsys.rcf.ActionRequest;
import com.agapsys.rcf.JsonRequest;
import com.agapsys.rcf.exceptions.BadRequestException;
import java.io.IOException;
import java.util.List;

public class AgresteJsonRequest extends AgresteRequest {
    
    public AgresteJsonRequest(ActionRequest wrappedRequest) {
        super(new JsonRequest(wrappedRequest));
    }
    
    public final <T> T readObject(Class<T> targetClass) throws IOException, BadRequestException {
        return ((JsonRequest)getWrappedRequest()).readObject(targetClass);
    }
    
    public final <E> List<E> readList(Class<E> elementClass) throws IOException, BadRequestException {
        return ((JsonRequest)getWrappedRequest()).readList(elementClass);
    }
    
    public JpaTransaction getJpaTransaction() {
        return (JpaTransaction) getMetadata(JpaTransactionFilter.JPA_TRANSACTION_ATTRIBUTE);
    }

}
