package br.unb.cic.bionimbuz.communication.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import br.unb.cic.bionimbuz.communication.rest.RestClient;
import br.unb.cic.bionimbuz.communication.rest.request.LoginRequest;
import br.unb.cic.bionimbuz.communication.rest.request.RequestInfo;
import br.unb.cic.bionimbuz.communication.rest.response.LoginResponse;
import br.unb.cic.bionimbuz.configuration.Configuration;
import br.unb.cic.bionimbuz.configuration.ConfigurationLoader;

public class LoginRestClient implements RestClient {
	private static final String REST_LOGIN_URL = "/rest/login";
	private LoginRequest loginRequest;
	private WebTarget target;
	private Configuration config;

	@Override
	public void setup(Client client, RequestInfo reqInfo) {
		System.out.println("Login REST client initialized...");

		this.config = ConfigurationLoader.readConfiguration();
		this.target = client.target(config.getBionimbuzAddress());
		this.loginRequest = (LoginRequest) reqInfo;
	}

	@Override
	public void prepareTarget() {
		target = target.path(REST_LOGIN_URL);
		
		System.out.println("Dispatching request to URI (POST): " + target.getUri());
	}

	@Override
	public LoginResponse execute() {
		LoginResponse response = target
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(loginRequest, MediaType.APPLICATION_JSON), LoginResponse.class);
		
		return response;
	}

}
