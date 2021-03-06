/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2019
 *
 *  Creation Date: 23.09.2019
 *
 *******************************************************************************/
package org.oscm.identity;

import org.oscm.identity.exception.IdentityClientException;
import org.oscm.identity.model.AccessToken;
import org.oscm.identity.model.AccessType;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Client for accessing oscm-identity using client credentials flow. it requires from the client to
 * get access token first, validate it and then request wanted endpoint. All necessary settings are
 * stored in {@link IdentityConfiguration} object and it requires only id of the tenant.
 */
public class ApiIdentityClient extends IdentityClient {

  public ApiIdentityClient(IdentityConfiguration configuration) {
    super(configuration);
  }

  @Override
  void validate(IdentityConfiguration configuration) {
    validator.validateRequiredSettings(configuration);
  }

  @Override
  public String getAccessToken(AccessType accessType) throws IdentityClientException {

    validate(configuration);

    IdentityUrlBuilder builder = new IdentityUrlBuilder(configuration.getTenantId());
    String url = builder.buildGetAccessTokenUrl();

    AccessToken accessToken = new AccessToken();
    accessToken.setAccessType(accessType);

    Response response =
        client
            .target(url)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(accessToken, MediaType.APPLICATION_JSON));

    AccessToken token = IdentityClientHelper.handleResponse(response, AccessToken.class, url);

    return token.getAccessToken();
  }
}
