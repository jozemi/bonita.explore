package com.xerox.explore.bonita.api.create;

import java.util.UUID;

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
public class ProcessWithGetConnectorWithParamExample extends AbstractConnectorExample {

	private static final String REST_GET_CALL_URL = "http://localhost:8090/persons/";
	
	private static final String PROCESS_VARIABLE_NAME = "valueVariableName";
	
	private static final String PROCESS_VARIABLE_TYPE = String.class.getName();
	
	public static void main(String[] args) {
		ProcessWithGetConnectorWithParamExample pwgce = new ProcessWithGetConnectorWithParamExample();
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
		
		processBuilder.createNewInstance("ProcessGetConnector", UUID.randomUUID().toString())
					  .addActor("User", true)
					  .addData(PROCESS_VARIABLE_NAME, PROCESS_VARIABLE_TYPE, new ExpressionBuilder().createConstantStringExpression("Value"))
					  .addDescription("CreatedByExternalProgram")
					  .addStartEvent("Start")
					  .addAutomaticTask("AutomaticTask")
					  		.addConnector("CallREST", "rest-get", "1.0.0", ConnectorEvent.ON_ENTER)
					  			.addInput("url", new ExpressionBuilder().createGroovyScriptExpression("url()", "return \""+REST_GET_CALL_URL+"?variable=\"+"+PROCESS_VARIABLE_NAME+";", PROCESS_VARIABLE_TYPE, 
					  							     new ExpressionBuilder().createDataExpression(PROCESS_VARIABLE_NAME, PROCESS_VARIABLE_TYPE)))
					  .addEndEvent("End")
					  .addTransition("Start", "AutomaticTask")
					  .addTransition("AutomaticTask", "End");

		return processBuilder.done();
	}
	
	void addConnectorDetails(BusinessArchiveBuilder bab) throws Exception{
		
		// Add connector implementation
		bab.addConnectorImplementation(new BarResource("rest-get", IOUtils.toByteArray(ProcessWithGetConnectorWithParamExample.class.getResource("/rest-get.impl").openStream())))

		// Add jar dependencies of the connector
		.addClasspathResource(new BarResource("bonita-connector-rest-1.0.1.jar",IOUtils.toByteArray(ProcessWithGetConnectorWithParamExample.class.getResource("/dependencies/bonita-connector-rest-1.0.1.jar").openStream())))
		.addClasspathResource(new BarResource("guava-18.0.jar", IOUtils.toByteArray(ProcessWithGetConnectorWithParamExample.class.getResource("/dependencies/guava-18.0.jar").openStream())))
		.addClasspathResource(new BarResource("httpclient-4.3.6.jar", IOUtils.toByteArray(ProcessWithGetConnectorWithParamExample.class.getResource("/dependencies/httpclient-4.3.6.jar").openStream())))
		.addClasspathResource(new BarResource("httpcore-4.3.3.jar", IOUtils.toByteArray(ProcessWithGetConnectorWithParamExample.class.getResource("/dependencies/httpcore-4.3.3.jar").openStream())));
	}

}
