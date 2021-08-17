/*
 * Copyright (c) 2021 Karnak Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.karnak.backend.config;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.karnak.ExternalIDProvider;
import org.karnak.backend.data.entity.ExternalIDProviderEntity;
import org.karnak.backend.enums.ExternalIDProviderType;
import org.karnak.backend.service.ExternalIDProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalIDProviderConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExternalIDProviderConfig.class);

  @Autowired private ApplicationContext context;
  private static String EXTERNALID_PROVIDERS_DIRECTORY = "/externalid-providers";
  private static String CLASH_PATH = "org.karnak.ExternalIDProviderImpl";
  private static ExternalIDProviderConfig instance;
  private HashMap<String, ExternalIDProvider> externalIDProviderImplMap;
  private ExternalIDProviderService externalIDProviderService;

  @Autowired
  public ExternalIDProviderConfig(final ExternalIDProviderService externalIDProviderService) {
    this.externalIDProviderService = externalIDProviderService;
  }

  public static ExternalIDProviderConfig getInstance() {
    return instance;
  }

  @PostConstruct
  public void postConstruct() {
    instance = this;
  }

  private boolean allJarAreLoaded() {
    final Set<ExternalIDProviderEntity> externalIDProviderEntities =
        externalIDProviderService.getAllExternalIDProvider();
    final Set<ExternalIDProviderEntity> externalIDProviderImplEntities =
        externalIDProviderEntities.stream()
            .filter(
                externalIDProviderEntity ->
                    externalIDProviderEntity
                        .getExternalIDProviderType()
                        .equals(ExternalIDProviderType.EXTID_PROVIDER_IMPLEMENTATION))
            .collect(Collectors.toSet());

    boolean errorFlag = false;
    for (ExternalIDProviderEntity externalIDProviderImplEntity : externalIDProviderImplEntities) {
      if (!externalIDProviderImplMap.containsKey(externalIDProviderImplEntity.getJarName())) {
        LOGGER.error(
            "File not found in {}{}/{}",
            System.getProperty("user.dir"),
            EXTERNALID_PROVIDERS_DIRECTORY,
            externalIDProviderImplEntity.getJarName());
        errorFlag = true;
      }
    }
    return !errorFlag;
  }

  public void loadExternalIDImplClass(String directory, String classpath) {
    externalIDProviderImplMap = new HashMap<>();
    File pluginsDir = new File(System.getProperty("user.dir") + directory);
    if (pluginsDir.listFiles() != null) {
      for (File jar : pluginsDir.listFiles()) {
        try {
          ClassLoader loader =
              URLClassLoader.newInstance(
                  new URL[] {jar.toURI().toURL()}, getClass().getClassLoader());
          Class<?> clazz = Class.forName(classpath, true, loader);
          Class<? extends ExternalIDProvider> newClass = clazz.asSubclass(ExternalIDProvider.class);
          Constructor<? extends ExternalIDProvider> constructor = newClass.getConstructor();
          final ExternalIDProvider externalIDProvider = constructor.newInstance();
          final String jarName = jar.getName();
          externalIDProviderImplMap.put(jarName, externalIDProvider);
          addExternalIDProviderImplInDb(jarName);
          LOGGER.warn(
              "Implementation of an external id provider are loaded in {}{}/{}",
              System.getProperty("user.dir"),
              EXTERNALID_PROVIDERS_DIRECTORY,
              jarName);
        } catch (Exception e) {
          LOGGER.error(
              "Cannot not load correctly the externalID provider implementationthe with jar", e);
        }
      }
    }
  }

  private void addExternalIDProviderImplInDb(String jarName) {
    if (!externalIDProviderService.jarNameExist(jarName)) {
      final ExternalIDProviderEntity newExternalIDProviderEntity =
          new ExternalIDProviderEntity(
              false, ExternalIDProviderType.EXTID_PROVIDER_IMPLEMENTATION, jarName);
      externalIDProviderService.saveExternalIDProvider(newExternalIDProviderEntity);
    }
  }

  @Bean
  public HashMap<String, ExternalIDProvider> externalIDProviderImplMap() {
    loadExternalIDImplClass(EXTERNALID_PROVIDERS_DIRECTORY, CLASH_PATH);

    if (!allJarAreLoaded()) {
      SpringApplication.exit(context, () -> 0);
    }

    return externalIDProviderImplMap;
  }
}
