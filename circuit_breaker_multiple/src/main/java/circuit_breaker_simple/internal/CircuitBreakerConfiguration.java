package circuit_breaker_simple.internal;

import javax.inject.Inject;
import org.mule.runtime.api.store.ObjectStoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.lifecycle.Disposable;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;

/**
 * This class represents an extension configuration, values set in this class
 * are commonly used across multiple operations since they represent something
 * core from the extension.
 */
public class CircuitBreakerConfiguration implements Initialisable, Disposable, Startable, Stoppable {

	private final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerConfiguration.class);

	@Inject
	private ObjectStoreManager osm;
	
	@Override
	public void start() throws MuleException {
		LOGGER.info("\n start");
	}

	@Override
	public void stop() throws MuleException {
		LOGGER.info("\n stop");
	}

	@Override
	public void initialise() throws InitialisationException {
		LOGGER.info("\n initialise");
		StateProcessor.objectStoreManager = osm;
	}

	@Override
	public void dispose() {
		LOGGER.info("\n dispose)");
	}

}
