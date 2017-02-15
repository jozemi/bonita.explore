package com.xerox.explore.bonita.api;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder;
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor;
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping;
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition;
import org.bonitasoft.engine.bpm.process.InvalidProcessDefinitionException;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.expression.InvalidExpressionException;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.platform.UnknownUserException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.util.APITypeManager;

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
public abstract class AbstractConnectorExample {


	private static final String BONITA_ENGINE_URL = "http://localhost:8080";
	private static final String PASSWORD = "bpm";
	private static final String USER = "walter.bates";

	public void runExample() {
		try {
			
			// Create a new business archive
			BusinessArchiveBuilder bab = new BusinessArchiveBuilder()
				.createNewBusinessArchive()
				
				// Set the process and actor mapping
				.setProcessDefinition(getProcess())
				.setActorMapping(getActorMapping());
	
			addConnectorDetails(bab);	

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
	ActorMapping getActorMapping() {
		ActorMapping am = new ActorMapping();
		Actor a = new Actor("User");
		a.addGroup("Acme");
		am.addActor(a);
		return am;
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
	APISession getSession() throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException,
			UnknownUserException, LoginException {
		
		Map<String, String> settings = new HashMap<String, String>();
		settings.put("server.url", BONITA_ENGINE_URL);
		settings.put("application.name", "bonita");
		APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, settings);

		LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();		

		return loginAPI.login(USER, PASSWORD);
	}
	
	/**
	 * Create a the process
	 * @return
	 * @throws InvalidExpressionException
	 * @throws InvalidProcessDefinitionException 
	 */
	abstract DesignProcessDefinition getProcess() throws InvalidExpressionException, InvalidProcessDefinitionException;
	
	/**
	 * Add connector details
	 * 
	 * @param bab
	 * @throws Exception
	 */
	abstract void addConnectorDetails(BusinessArchiveBuilder bab) throws Exception;

}
