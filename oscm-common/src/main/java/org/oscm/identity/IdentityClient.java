/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: 18.09.2019
 *
 *******************************************************************************/
package org.oscm.identity;

import org.oscm.identity.exception.IdentityClientException;
import org.oscm.identity.model.GroupInfo;
import org.oscm.identity.model.Token;
import org.oscm.identity.model.UserInfo;
import org.oscm.identity.validator.IdentityValidator;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.validation.ArgumentValidator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Common class for accessing oscm-identity endpoints
 */
public abstract class IdentityClient {

  private static final Log4jLogger LOGGER = LoggerFactory.getLogger(IdentityClient.class);
  private Client client = ClientBuilder.newClient();

  IdentityValidator validator = new IdentityValidator();
  IdentityConfiguration configuration;

  IdentityClient(IdentityConfiguration configuration) {
    this.configuration = configuration;
  }

  UserInfo getUser(String accessToken, String userId) throws IdentityClientException {

    ArgumentValidator.notEmptyString("accessToken", accessToken);
    ArgumentValidator.notEmptyString("userId", userId);
    validator.validateRequiredSettings(configuration);

    IdentityUrlBuilder builder = new IdentityUrlBuilder(configuration.getTenantId());
    String url = builder.buildGetUserUrl();

    Response response =
        client
            .target(url)
            .path(userId)
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .get();

    logResponseInfo(url, response);
    IdentityClientHelper.handlePossibleErrorResponse(response);

    UserInfo userInfo = response.readEntity(UserInfo.class);
    return userInfo;
  }

  Token refreshToken(String refreshToken) throws IdentityClientException {

    ArgumentValidator.notEmptyString("refreshToken", refreshToken);
    validator.validateRequiredSettings(configuration);

    IdentityUrlBuilder builder = new IdentityUrlBuilder(configuration.getTenantId());
    String url = builder.buildRefreshTokenUrl();

    Token token = new Token();
    token.setRefreshToken(refreshToken);

    Response response =
        client
            .target(url)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(token, MediaType.APPLICATION_JSON));

    logResponseInfo(url, response);
    IdentityClientHelper.handlePossibleErrorResponse(response);

    Token refreshedToken = response.readEntity(Token.class);
    return refreshedToken;
  }

  Token getAccessToken() throws IdentityClientException {

    validator.validateRequiredSettings(configuration);

    IdentityUrlBuilder builder = new IdentityUrlBuilder(configuration.getTenantId());
    String url = builder.buildGetAccessTokenUrl();

    Response response =
        client
            .target(url)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity("", MediaType.APPLICATION_JSON));

    logResponseInfo(url, response);
    IdentityClientHelper.handlePossibleErrorResponse(response);

    Token token = response.readEntity(Token.class);
    return token;
  }

  GroupInfo createGroup(String accessToken, String groupName, String groupDescription)
      throws IdentityClientException {

    ArgumentValidator.notEmptyString("accessToken", accessToken);
    ArgumentValidator.notEmptyString("groupName", groupName);
    validator.validateRequiredSettings(configuration);

    IdentityUrlBuilder builder = new IdentityUrlBuilder(configuration.getTenantId());
    String url = builder.buildCreateGroupUrl();

    GroupInfo groupInfo = new GroupInfo();
    groupInfo.setName(groupName);
    groupInfo.setDescription(groupDescription);

    Response response =
        client
            .target(url)
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .post(Entity.entity(groupInfo, MediaType.APPLICATION_JSON));

    logResponseInfo(url, response);
    IdentityClientHelper.handlePossibleErrorResponse(response);

    GroupInfo group = response.readEntity(GroupInfo.class);
    return group;
  }

  private void logResponseInfo(String requestedUrl, Response response) {

    int status = response.getStatus();
    LOGGER.logInfo(
        Log4jLogger.SYSTEM_LOG,
        LogMessageIdentifier.INFO_IDENTITY_CLIENT_RESPONSE,
        requestedUrl,
        Integer.toString(status));
  }
}
