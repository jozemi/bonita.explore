package com.xerox.explore.bonita.api;

import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.bonitasoft.engine.bpm.bar.BarResource;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder;
import org.bonitasoft.engine.bpm.connector.ConnectorEvent;
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition;
import org.bonitasoft.engine.bpm.process.InvalidProcessDefinitionException;
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder;
import org.bonitasoft.engine.expression.ExpressionBuilder;
import org.bonitasoft.engine.expression.ExpressionConstants;
import org.bonitasoft.engine.expression.ExpressionInterpreter;
import org.bonitasoft.engine.expression.ExpressionType;
import org.bonitasoft.engine.expression.InvalidExpressionException;
import org.bonitasoft.engine.expression.Expression;
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
public class ProcessWithPostConnectorUsingProcessVariableExample extends AbstractConnectorExample {

	private static final String REST_POST_CALL_URL = "http://localhost:8090/persons/";
	
	private static final String REST_POST_CONTENT_TYPE = "application/json";
	
	private static final String PROCESS_VARIABLE_NAME = "valueVariableName";
	
	private static final String PROCESS_VARIABLE_TYPE = String.class.getName();
	
	public static void main(String[] args) {
		ProcessWithPostConnectorExample pwgce = new ProcessWithPostConnectorExample();
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
		
		processBuilder.createNewInstance("ProcessPostConnector", UUID.randomUUID().toString())
						.addActor("User", true)
						.addData(PROCESS_VARIABLE_NAME, PROCESS_VARIABLE_TYPE, new ExpressionBuilder().createConstantStringExpression("Value"))
					  .addDescription("CreatedByExternalProgram")
					  .addStartEvent("Start")
					  .addAutomaticTask("AutomaticTask")
					  		.addConnector("CallREST", "rest-post", "1.0.0", ConnectorEvent.ON_ENTER)
					  			.addInput("url", new ExpressionBuilder().createConstantStringExpression(REST_POST_CALL_URL))
					  			.addInput("contentType", new ExpressionBuilder().createConstantStringExpression(REST_POST_CONTENT_TYPE))
					  			.addInput("charset", new ExpressionBuilder().createConstantStringExpression("UTF-8"))
					  			.addInput("body", new ExpressionBuilder().createPatternExpression("name", "{\"name\":\"${valueVariableName}\"}", 
					  					new ExpressionBuilder().createGroovyScriptExpression(PROCESS_VARIABLE_NAME, PROCESS_VARIABLE_NAME, PROCESS_VARIABLE_TYPE, 
										new ExpressionBuilder().createDataExpression(PROCESS_VARIABLE_NAME, PROCESS_VARIABLE_TYPE))))
					  .addEndEvent("End")
					  .addTransition("Start", "AutomaticTask")
					  .addTransition("AutomaticTask", "End");

		return processBuilder.done();
	}
	
	void addConnectorDetails(BusinessArchiveBuilder bab) throws Exception{
		
		// Add connector implementation
		bab.addConnectorImplementation(new BarResource("rest-post", IOUtils.toByteArray(ProcessWithGetConnectorExample.class.getResource("/rest-post.impl").openStream())))

		// Add jar dependencies of the connector
		.addClasspathResource(new BarResource("bonita-connector-rest-1.0.1.jar",IOUtils.toByteArray(ProcessWithGetConnectorExample.class.getResource("/dependencies/bonita-connector-rest-1.0.1.jar").openStream())))
		.addClasspathResource(new BarResource("guava-18.0.jar", IOUtils.toByteArray(ProcessWithGetConnectorExample.class.getResource("/dependencies/guava-18.0.jar").openStream())))
		.addClasspathResource(new BarResource("httpclient-4.3.6.jar", IOUtils.toByteArray(ProcessWithGetConnectorExample.class.getResource("/dependencies/httpclient-4.3.6.jar").openStream())))
		.addClasspathResource(new BarResource("httpcore-4.3.3.jar", IOUtils.toByteArray(ProcessWithGetConnectorExample.class.getResource("/dependencies/httpcore-4.3.3.jar").openStream())));
	}

}
