package circuit_breaker_simple.internal;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.RouterCompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircuitBreakerRouter {
	
	private final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerRouter.class);
	
	@DisplayName("Circuit Breaker")
	public void circuitBreaker(
			
			@DisplayName("Circuit Breaker Instance ID")
			String circuitBreakerId,
			
			@Optional(defaultValue="2")
			@DisplayName("Closed error threshold")
			Integer closedErrorThreshold,
			
			@Optional(defaultValue="5000")
			@DisplayName("Closed refresh period")
			Integer closedRefreshPeriod,
			
			@Optional(defaultValue="5000")
			@DisplayName("Open period")
			Integer openPeriod,
			
			ClosedRoute closedRoute, 
			OpenRoute openRoute, 
			RouterCompletionCallback callback ) {
					
		// determine circuit state for this circuitBreakerId instance
		CBState cbState = StateProcessor.determineCBState(circuitBreakerId, closedErrorThreshold, closedRefreshPeriod, openPeriod);
		
		switch(cbState) {
			case CLOSED: {
				LOGGER.info("\n In CLOSED case");
				closedRoute.getChain().process(callback::success, (error, previous) -> {
					StateProcessor.incrementErrorCount(circuitBreakerId);			
					callback.error(error);
				});
				break;
			}
			case OPEN: {
				LOGGER.info("\n In OPEN case");
				openRoute.getChain().process(callback::success, (e, r) -> callback.error(e));
				break;
			}
			default: {
				LOGGER.info("\n In DEFAULT case");
				callback.success(Result.builder().build());
				LOGGER.info("Default state executed");
			}
				
		}

	}
}
