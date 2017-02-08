package com.xerox.explore.bonita.api;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.bar.BarResource;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder;
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor;
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping;
import org.bonitasoft.engine.bpm.connector.ConnectorEvent;
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition;
import org.bonitasoft.engine.bpm.process.InvalidProcessDefinitionException;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.expression.ExpressionBuilder;
import org.bonitasoft.engine.expression.InvalidExpressionException;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.platform.UnknownUserException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.util.APITypeManager;

import org.apache.commons.io.IOUtils;

/**
 * Create, deploy and enable a Bonita process programmatically and out-of-the-box.
 * 
 * The process use a connector to use a REST API.
 * 
 * @author José Miguel Pérez-Álvarez
 * @author Mario Cortes Cornax
 * @author Adrian Mos
 *
 */
public class ProcessWithConnectorExample {


	private static final String BONITA_ENGINE_URL = "http://localhost:8080";
	private static final String PASSWORD = "bpm";
	private static final String USER = "walter.bates";
	
	private static final String REST_GET_CALL_URL = "http://localhost:8090/persons/";

	
	public static void main(String[] args) {
		try {
			// Create a new business archive
			BusinessArchiveBuilder bab = new BusinessArchiveBuilder()
				.createNewBusinessArchive()
				
				// Set the process and actor mapping
				.setProcessDefinition(getProcess())
				.setActorMapping(getActorMapping())
	
				// Add connector implementation
				.addConnectorImplementation(new BarResource("rest-get", IOUtils.toByteArray(ProcessWithConnectorExample.class.getResource("/rest-get.impl").openStream())))
	
				// Add jar dependencies of the connector
				.addClasspathResource(new BarResource("bonita-connector-rest-1.0.1.jar",IOUtils.toByteArray(ProcessWithConnectorExample.class.getResource("/dependencies/bonita-connector-rest-1.0.1.jar").openStream())))
				.addClasspathResource(new BarResource("guava-18.0.jar", IOUtils.toByteArray(ProcessWithConnectorExample.class.getResource("/dependencies/guava-18.0.jar").openStream())))
				.addClasspathResource(new BarResource("httpclient-4.3.6.jar", IOUtils.toByteArray(ProcessWithConnectorExample.class.getResource("/dependencies/httpclient-4.3.6.jar").openStream())))
				.addClasspathResource(new BarResource("httpcore-4.3.3.jar", IOUtils.toByteArray(ProcessWithConnectorExample.class.getResource("/dependencies/httpcore-4.3.3.jar").openStream())));

			// Deploy and enable the process
			APISession apiSession = getSession();
			ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

			ProcessDefinition processDefinition = processAPI.deploy(bab.done());
			processAPI.enableProcess(processDefinition.getId());

		} catch (Exception e) {
			System.err.println("Ups, something is wrong");
		}

	}

	/**
	 * Mapping 
	 * 
	 * @return
	 */
	private static ActorMapping getActorMapping() {
		ActorMapping am = new ActorMapping();
		Actor a = new Actor("User");
		a.addGroup("Acme");
		am.addActor(a);
		return am;
	}

	/**
	 * Create a the process
	 * @return
	 * @throws InvalidExpressionException
	 * @throws InvalidProcessDefinitionException 
	 */
	private static DesignProcessDefinition getProcess() throws InvalidExpressionException, InvalidProcessDefinitionException {
		ProcessDefinitionBuilder processBuilder = new ProcessDefinitionBuilder();
		
		processBuilder.createNewInstance("CreatedOutOfBonita2", "1.10")
					  .addActor("User", true)
					  .addDescription("CreatedByExternalProgram")
					  .addStartEvent("Start")
					  .addAutomaticTask("AutomaticTask")
					  		.addConnector("CallREST", "rest-get", "1.0.0", ConnectorEvent.ON_ENTER)
					  			.addInput("url", new ExpressionBuilder().createConstantStringExpression(REST_GET_CALL_URL))
					  .addEndEvent("End")
					  .addTransition("Start", "AutomaticTask")
					  .addTransition("AutomaticTask", "End");

		return processBuilder.done();
	}

	/**
	 * Create a session
	 * 
	 * @return
	 * @throws BonitaHomeNotSetException
	 * @throws ServerAPIException
	 * @throws UnknownAPITypeException
	 * @throws UnknownUserException
	 * @throws LoginException
	 */
	private static APISession getSession() throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException,
			UnknownUserException, LoginException {
		
		Map<String, String> settings = new HashMap<String, String>();
		settings.put("server.url", BONITA_ENGINE_URL);
		settings.put("application.name", "bonita");
		APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, settings);

		LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();		

		return loginAPI.login(USER, PASSWORD);
	}

}
