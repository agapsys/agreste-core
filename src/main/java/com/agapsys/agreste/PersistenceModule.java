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

import com.agapsys.web.toolkit.AbstractApplication;
import java.util.logging.Level;

public class PersistenceModule extends com.agapsys.web.toolkit.modules.PersistenceModule {
    
    /**
     * Return hibernate logging level.
     * @return hibernate logging level. Default implementation disables log.
     */
    protected Level getHibernateLogLevel() {
        return Level.OFF;
    }

    @Override
    protected void onInit(AbstractApplication app) {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(getHibernateLogLevel());

        super.onInit(app);
    }
    
}
