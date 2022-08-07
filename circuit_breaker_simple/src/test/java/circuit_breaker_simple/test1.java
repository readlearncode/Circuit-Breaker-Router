package circuit_breaker_simple;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.event.Event;

public class test1 extends MuleArtifactFunctionalTestCase {

//	@Test
//	public void test() throws Exception {
//		Event event = flowRunner("circuit-breakerFlow").run();
//		Object payloadValue = event.getMessage().getPayload().getValue();
//
//		
//		assertTrue(true);
//
//	}

	@Test
	public void executeSayHiOperation() throws Exception {
	  Event sayHiFlow = flowRunner("sayHiFlow").run();           
	  String payloadValue = ((String) sayHiFlow
	                                    .getMessage()
	                                    .getPayload()
	                                    .getValue());            
	  assertTrue(true);
	}
}
