/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.rest;


import com.cognifide.aet.communication.api.CommunicationSettings;
import com.cognifide.aet.rest.helpers.FreeMarkerConfigurationManager;
import com.cognifide.aet.rest.helpers.ReportConfigurationManager;
import com.cognifide.aet.rest.helpers.SuitesListProvider;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.SuiteQueryWrapper;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(immediate = true)
public class ConfigsServlet extends HttpServlet {

  private static final long serialVersionUID = -8757845025924429010L;

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigsServlet.class);

  private static final Gson GSON = new Gson();

  private static final String COMPANY_PARAM = "company";

  private static final String PROJECT_PARAM = "project";

  public static final String LOCKS_PARAM = "locks";

  public static final String LIST_PARAM = "list";

  public static final String COMMUNICATION_SETTINGS_PARAM = "communicationSettings";

  @Reference
  private transient HttpService httpService;

  @Reference
  private transient MetadataDAO metadataDAO;

  @Reference
  private transient LockService lockService;

  @Reference
  private transient ReportConfigurationManager reportConfigurationManager;

  @Reference
  private transient FreeMarkerConfigurationManager templateConfiguration;


  /***
   * Returns JSON representation of Suite based on correlationId or suite name.
   * If suite name is provided, then newest version of JSON is returned.
   *
   * @param req
   * @param resp
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    LOGGER.debug("GET, req: '{}'", req);
    PrintWriter responseWriter = retrieveResponseWriter(req, resp);
    if (responseWriter != null) {
      resp.setContentType("application/json");
      String configType = StringUtils.substringAfter(req.getRequestURI(), Helper.getConfigsPath())
          .replace(Helper.PATH_SEPARATOR, "");
      String reportDomain = reportConfigurationManager.getReportDomain();

      if (COMMUNICATION_SETTINGS_PARAM.equals(configType)) {
        CommunicationSettings communicationSettings = new CommunicationSettings(reportDomain);
        responseWriter.write(GSON.toJson(communicationSettings));
      } else if (LIST_PARAM.equals(configType)) {
        resp.setContentType("text/html");

        processListRequest(req, resp);
      } else if (LOCKS_PARAM.equals(configType)) {
        responseWriter.write(getLocks());
      } else {
        resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
        responseWriter.write("Unable to get given config.");
      }
    } else {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    flushResponseBuffer(req, resp);
  }

  private void processListRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String reportDomain = reportConfigurationManager.getReportDomain();
    Map<DBKey, List<SuiteQueryWrapper>> data = new SuitesListProvider(metadataDAO, reportDomain).listProjects();
    Map root = new HashMap();
    root.put("data", data);
    root.put("size", data.size());
    root.put("reportDomain", reportDomain);

    Template template = templateConfiguration.getConfiguration().getTemplate("suitesList.ftlh");

    try {
      template.process(root, resp.getWriter());
    } catch (TemplateException e) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      LOGGER.error("Template engine error" ,e);
    }

  }

  private boolean hasParameters(HttpServletRequest req) {
    Map parameters = req.getParameterMap();
    return parameters.containsKey(COMPANY_PARAM) && parameters.containsKey(PROJECT_PARAM);
  }

  private PrintWriter retrieveResponseWriter(HttpServletRequest req, HttpServletResponse resp) {
    PrintWriter printWriter = null;
    try {
      printWriter = resp.getWriter();
    } catch (IOException e) {
      LOGGER.error("error while getting response writer for request: '{}' and response: '{}'", req,
          resp, e);
    }
    return printWriter;
  }

  private void flushResponseBuffer(HttpServletRequest req, HttpServletResponse resp) {
    try {
      resp.flushBuffer();
    } catch (IOException e) {
      LOGGER.error("error while flushing response buffer for request: '{}' and response: '{}'", req,
          resp, e);
    }
  }

  private String getLocks() {
    return GSON.toJson(lockService.getAllLocks());
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    LOGGER.info("Initializing " + config.getServletName());
  }

  @Activate
  public void start() {
    LOGGER.debug("Registering servlet at " + Helper.getConfigsPath());
    try {
      httpService.registerServlet(Helper.getConfigsPath(), this, null,
          null);
    } catch (Exception e) {
      LOGGER.error("Failed to register servlet at " + Helper.getConfigsPath(), e);
    }
  }

  @Deactivate
  public void stop() {
    httpService.unregister(Helper.getConfigsPath());
    httpService = null;
  }


}

