package circuit_breaker_simple;

import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;

public class ModuleTestCase extends MuleArtifactFunctionalTestCase {
    @Override
    protected String getConfigFile() {
       // return "circuit-breaker.xml";
    	 return "test-connector-mule-app.xml";
    }
}
