/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.plugin.sdk.server;

import static org.eclipse.che.plugin.sdk.shared.Constants.CHE_PLUGIN_GENERATION_STRATEGY;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.fs.server.FsManager;
import org.eclipse.che.api.project.server.type.AttributeValue;
import org.eclipse.che.plugin.maven.server.projecttype.handler.GeneratorStrategy;

/** Generates content for Maven project with Che plugin. */
@Singleton
public class ChePluginGeneratorStrategy implements GeneratorStrategy {

  private final FsManager fsManager;

  @Inject
  public ChePluginGeneratorStrategy(FsManager fsManager) {
    this.fsManager = fsManager;
  }

  @Override
  public String getId() {
    return CHE_PLUGIN_GENERATION_STRATEGY;
  }

  @Override
  public void generateProject(
      String projectPath, Map<String, AttributeValue> attributes, Map<String, String> options)
      throws ForbiddenException, ConflictException, ServerException, NotFoundException {
    fsManager.createDir(projectPath);

    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

    URL zipUrl = contextClassLoader.getResource("template.zip");
    if (zipUrl == null) {
      throw new NotFoundException("Template not found.");
    }

    try (InputStream zip = zipUrl.openStream()) {
      fsManager.unzip(projectPath, zip, false);
    } catch (IOException e) {
      throw new ServerException(e);
    }
  }
}