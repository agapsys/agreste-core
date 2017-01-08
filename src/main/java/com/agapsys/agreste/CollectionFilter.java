/*
 * Copyright 2016 Agapsys Tecnologia Ltda-ME.
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

/**
 * Defines filtering criteria for elements in a collection transformation
 * @author Leandro Oliveira (leandro@agapsys.com)
 * @param <S> Source type
 */
public interface CollectionFilter<S> {
    /**
     * Returns a boolean indicating if an object shall be processed while transforming a source collection
     * @param srcObj source object
     * @return a boolean indicating if an object shall be processed while transforming a source collection
     */
    public boolean process(S srcObj);
}
