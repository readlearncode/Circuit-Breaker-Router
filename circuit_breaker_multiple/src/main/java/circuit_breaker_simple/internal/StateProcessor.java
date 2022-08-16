package circuit_breaker_simple.internal;

import java.time.Instant;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.api.store.ObjectStoreException;
import org.mule.runtime.api.store.ObjectStoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateProcessor {
	private final static Logger LOGGER = LoggerFactory.getLogger(StateProcessor.class);
	
	public static ObjectStoreManager objectStoreManager;

	public static synchronized CBState determineCBState(String circuitBreakerId, Integer closedErrorThreshold, Integer closedRefreshPeriod, Integer openPeriod) {

		LOGGER.info("\n Determine CBState for circuitBreakerId = " + circuitBreakerId);
		
		ObjectStore<CircuitBreaker> os = objectStoreManager.getDefaultPartition();
		CircuitBreaker circuitBreaker = null;
		
		try {
			if (os.contains(circuitBreakerId)) {
				
				// Retrieve CB object associated with CB instance id
				LOGGER.info("Retriving CB with id: " + circuitBreakerId);
				circuitBreaker = os.retrieve(circuitBreakerId);
			
				// Calculate elapsed time
				Long timeSinceEpoch = circuitBreaker.getTimeSinceEpoch();
				Long currentTimeSinceEpoch =  Instant.now().toEpochMilli();
				Long timeElapsedSinceStatusCheck = currentTimeSinceEpoch - timeSinceEpoch;		

				// evaluate current CB state
				switch(circuitBreaker.getCbState()) {
					case CLOSED: {	
						if (timeElapsedSinceStatusCheck >= closedRefreshPeriod) {
							circuitBreaker.setErrorCount(0);
							circuitBreaker.setTimeSinceEpoch(currentTimeSinceEpoch);
							updateCBObjectInOBStore(circuitBreakerId, os, circuitBreaker);
							return CBState.CLOSED;
							
						} else if (circuitBreaker.getErrorCount() >= closedErrorThreshold) {
							circuitBreaker.setErrorCount(0);
							circuitBreaker.setTimeSinceEpoch(currentTimeSinceEpoch);
							circuitBreaker.setCbState(CBState.OPEN);
							updateCBObjectInOBStore(circuitBreakerId, os, circuitBreaker);
							return CBState.OPEN;
							
						} else {
							return CBState.CLOSED;
						}			
					}										
					case OPEN: {
						if (timeElapsedSinceStatusCheck >= openPeriod) {
							circuitBreaker.setTimeSinceEpoch(currentTimeSinceEpoch);
							circuitBreaker.setCbState(CBState.CLOSED);
							updateCBObjectInOBStore(circuitBreakerId, os, circuitBreaker);
							return CBState.CLOSED;
						} else {
							return CBState.OPEN;
						}
					}
					
					default: {
						LOGGER.info("default case: " + circuitBreaker);
					}
					

				}
			} else {
				// if not exists, then create, add current time since epoch and update state to CLOSED anf return CLOSED state
				LOGGER.info("Creating new CB: " + circuitBreakerId);
				circuitBreaker = new CircuitBreaker(circuitBreakerId, openPeriod, openPeriod, openPeriod);
				os.store(circuitBreakerId, circuitBreaker);
				return CBState.CLOSED;
			}
			

			
			
		} catch (ObjectStoreException e) {
			LOGGER.info("An issue interacting with the object store: " + circuitBreakerId);
			e.printStackTrace();
		} finally {
			LOGGER.info("Current state of the Circuit Breaker" + circuitBreaker);
		}
		
		
		return CBState.CLOSED;
		
	}

	private static void updateCBObjectInOBStore(String circuitBreakerId, ObjectStore<CircuitBreaker> os,
			CircuitBreaker circuitBreaker) throws ObjectStoreException {
		// Update OB with new CB instance
		os.remove(circuitBreakerId);
		os.store(circuitBreakerId, circuitBreaker);
	}

	public static synchronized void incrementErrorCount(String circuitBreakerId) {
		
		try {
			// retrieve circuit breaker from OB store
			ObjectStore<CircuitBreaker> os = objectStoreManager.getDefaultPartition();
			CircuitBreaker circuitBreaker = os.retrieve(circuitBreakerId);
			// update error count
			circuitBreaker.incrementErrorCount();
			updateCBObjectInOBStore(circuitBreakerId, os, circuitBreaker);
		} catch (ObjectStoreException e) {
			e.printStackTrace();
		}
	}


}
