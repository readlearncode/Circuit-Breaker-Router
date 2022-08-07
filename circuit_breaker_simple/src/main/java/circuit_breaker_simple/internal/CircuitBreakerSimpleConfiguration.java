package circuit_breaker_simple.internal;

import javax.inject.Inject;
import org.mule.runtime.api.store.ObjectStoreManager;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;
/**
 * This class represents an extension configuration, values set in this class
 * are commonly used across multiple operations since they represent something
 * core from the extension.
 */
public class CircuitBreakerSimpleConfiguration implements Startable, Stoppable{

	private final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerSimpleConfiguration.class);

	@ParameterGroup(name="CBConfig")
	private CircuitBreakerParameters cBconfig;
	
	@Inject
	private ObjectStoreManager osm;

	@Override
	public void start() throws MuleException {	
		StateProcessor.setStateProcessor(osm, cBconfig);
		LOGGER.info("Set StateProcessor with osm and CBConfigs ");
	}
	
	@Override
	public void stop() throws MuleException {}


}
