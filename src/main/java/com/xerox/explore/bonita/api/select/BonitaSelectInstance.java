package com.xerox.explore.bonita.api.select;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.LoginAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.flownode.ArchivedActivityInstanceSearchDescriptor;
import org.bonitasoft.engine.bpm.process.ArchivedProcessInstance;
import org.bonitasoft.engine.bpm.process.ArchivedProcessInstancesSearchDescriptor;
import org.bonitasoft.engine.bpm.process.ProcessInstanceState;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.platform.UnknownUserException;
import org.bonitasoft.engine.search.Order;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.util.APITypeManager;

/**
 * 
 * @author Jose Miguel Perez
 *
 */
public class BonitaSelectInstance {

	private static final String BONITA_ENGINE_URL = "http://localhost:8080";
	private static final String USER = "walter.bates";
	private static final String PASSWORD = "bpm";
	
	private static final String PROCESS_NAME = "process";

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			BonitaSelectInstance main = new BonitaSelectInstance();

			SearchResult<ArchivedProcessInstance> instancesQ1 = main.searchByNameAndStatus(PROCESS_NAME, ProcessInstanceState.CANCELLED.getId());
			System.out.println("Found "+instancesQ1.getCount()+" results ");
			SearchResult<ArchivedProcessInstance> instancesQ2 = main.searchByNameAndStatus(PROCESS_NAME, ProcessInstanceState.COMPLETED.getId());
			System.out.println("Found "+instancesQ2.getCount()+" results ");
			
			
		} catch (Exception e) {
			System.err.println("Ups, something is wrong");
		}

	}

	/**
	 * Search by name and status
	 * 
	 * @param name
	 * @param status
	 * @return
	 * @throws UnknownUserException
	 * @throws SearchException
	 * @throws BonitaHomeNotSetException
	 * @throws ServerAPIException
	 * @throws UnknownAPITypeException
	 * @throws LoginException
	 */
	public SearchResult<ArchivedProcessInstance> searchByNameAndStatus(String name, int status)
			throws UnknownUserException, SearchException, BonitaHomeNotSetException, ServerAPIException,
			UnknownAPITypeException, LoginException {
		SearchOptionsBuilder searchBuilder = new SearchOptionsBuilder(0, -1);
		searchBuilder.filter(ArchivedProcessInstancesSearchDescriptor.NAME, name);
		searchBuilder.filter(ArchivedProcessInstancesSearchDescriptor.STATE_ID, status);
		searchBuilder.sort(ArchivedActivityInstanceSearchDescriptor.NAME, Order.ASC);
		return TenantAPIAccessor.getProcessAPI(getSession()).searchArchivedProcessInstances(searchBuilder.done());
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

}
