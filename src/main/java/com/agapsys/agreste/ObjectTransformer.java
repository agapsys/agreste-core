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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Transforms an object into another
 * @author Leandro Oliveira (leandro@agapsys.com)
 * @param <S> Source type
 * @param <D> Destination type
 */
public abstract class ObjectTransformer<S, D> {
	// STATIC SCOPE ============================================================
	private static <D> Collection<D> getEmptyCollection(Collection srcCollection) {
		Collection<D> destCollection;

		if (srcCollection instanceof Set) {
			destCollection = new LinkedHashSet<>();
		} else if (srcCollection instanceof List) {
			destCollection = new LinkedList<>();
		} else {
			throw new UnsupportedOperationException("Unsupported collection: " + srcCollection.getClass().getName());
		}
		
		return destCollection;
	}
	
	/**
	 * Transforms a collection into another
	 * @param <D> Destination collection element type
	 * @param destinationElementClass Destination collection element type. It's assumed that this class will have a constructor which receives an object of the same class of the elements in source collection.
	 * @param srcCollection source collection
	 * @return transformed collection
	 */
	public static <D> Collection<D> getCollection(Class<D> destinationElementClass, Collection srcCollection) {
		Collection<D> destCollection = getEmptyCollection(srcCollection);
		
		try {
			for (Object element : srcCollection) {
				destCollection.add(destinationElementClass.getConstructor(element.getClass()).newInstance(element));
			}
			
			return destCollection;
			
		} catch (Throwable t) {
			if (t instanceof RuntimeException)
				throw (RuntimeException) t;
			
			throw new RuntimeException(t);
		}
	}
	
	/** Convenience method for getCollection(srcCollection, transformer, null). */
	public static <D> Collection<D> getCollection(Collection srcCollection, ObjectTransformer transformer) {
		return getCollection(srcCollection, transformer, null);
	}

	/**
	 * Transforms a collection into another collection.
	 * @param <D> Destination collection generic type
	 * @param srcCollection source collection
	 * @param transformer object transformer
	 * @param filter collection transform filter. Passing <code>null</code> means no filtering (all elements will be transformed).
	 * @return transformed collection
	 */
	public static <D> Collection<D> getCollection(Collection srcCollection, ObjectTransformer transformer, CollectionFilter filter) {
		Collection<D> destCollection = getEmptyCollection(srcCollection);

		for (Object object : srcCollection) {
			if (filter != null && !filter.process(object))
				continue;

			D destObj = (D) transformer.getFrom(object);
			destCollection.add(destObj);
		}

		return destCollection;
	}
	// =========================================================================

	// INSTANCE SCOPE ==========================================================
	public abstract D getFrom(S s);

	public final Collection<D> getCollection(Collection<S> srcCollection) {
		return getCollection(srcCollection, (CollectionFilter)null);
	}

	public final Collection<D> getCollection(Collection<S> srcCollection, CollectionFilter filter) {
		return getCollection(srcCollection, this, filter);
	}
	// =========================================================================
}
