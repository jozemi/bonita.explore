package com.xerox.explore.bonita.api;

import org.apache.commons.io.IOUtils;
import org.bonitasoft.engine.bpm.bar.BarResource;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder;
import org.bonitasoft.engine.bpm.connector.ConnectorEvent;
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition;
import org.bonitasoft.engine.bpm.process.InvalidProcessDefinitionException;
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder;
import org.bonitasoft.engine.expression.ExpressionBuilder;
import org.bonitasoft.engine.expression.InvalidExpressionException;

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
public class ProcessWithGetConnectorExample extends AbstractConnectorExample {

	private static final String REST_GET_CALL_URL = "http://localhost:8090/persons/";
	
	public static void main(String[] args) {
		ProcessWithGetConnectorExample pwgce = new ProcessWithGetConnectorExample();
		pwgce.runExample();
	}
	
	/**
	 * Create a the process
	 * @return
	 * @throws InvalidExpressionException
	 * @throws InvalidProcessDefinitionException 
	 */
	DesignProcessDefinition getProcess() throws InvalidExpressionException, InvalidProcessDefinitionException {
		ProcessDefinitionBuilder processBuilder = new ProcessDefinitionBuilder();
		
		processBuilder.createNewInstance("ProcessGetConnector", "1.10")
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
	
	void addConnectorDetails(BusinessArchiveBuilder bab) throws Exception{
		
		// Add connector implementation
		bab.addConnectorImplementation(new BarResource("rest-get", IOUtils.toByteArray(ProcessWithGetConnectorExample.class.getResource("/rest-get.impl").openStream())))

		// Add jar dependencies of the connector
		.addClasspathResource(new BarResource("bonita-connector-rest-1.0.1.jar",IOUtils.toByteArray(ProcessWithGetConnectorExample.class.getResource("/dependencies/bonita-connector-rest-1.0.1.jar").openStream())))
		.addClasspathResource(new BarResource("guava-18.0.jar", IOUtils.toByteArray(ProcessWithGetConnectorExample.class.getResource("/dependencies/guava-18.0.jar").openStream())))
		.addClasspathResource(new BarResource("httpclient-4.3.6.jar", IOUtils.toByteArray(ProcessWithGetConnectorExample.class.getResource("/dependencies/httpclient-4.3.6.jar").openStream())))
		.addClasspathResource(new BarResource("httpcore-4.3.3.jar", IOUtils.toByteArray(ProcessWithGetConnectorExample.class.getResource("/dependencies/httpcore-4.3.3.jar").openStream())));
	}

}
