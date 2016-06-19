/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.gora.jcache.store;

import org.apache.gora.persistency.impl.PersistentBase;

import javax.cache.configuration.Factory;

public class JCacheCacheFactoryBuilder {

  public static <K, T extends PersistentBase> Factory<JCacheCacheLoader<K,T>>
  factoryOfCacheLoader(Class<K> keyClass, Class<T> persistentClass) {
    return new JCacheCacheLoaderFactory<>(keyClass, persistentClass);
  }

  public static <K, T extends PersistentBase> Factory<JCacheCacheWriter<K,T>>
  factoryOfCacheWriter(Class<K> keyClass, Class<T> persistentClass) {
    return new JCacheCacheWriterFactory<>(keyClass, persistentClass);
  }

  public static <K,T extends PersistentBase> Factory<JCacheCacheEntryListener<K, T>>
  factoryOfEntryListener(JCacheCacheEntryListener<K, T> instance) {
    return new JCacheCacheEntryListenerFactory<>(instance);
  }

}
