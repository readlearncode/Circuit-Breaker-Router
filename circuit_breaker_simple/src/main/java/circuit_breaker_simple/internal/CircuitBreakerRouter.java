package circuit_breaker_simple.internal;

import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.RouterCompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircuitBreakerRouter {
	
	private final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerRouter.class);
	
	@DisplayName("Circuit Breaker")
	public void circuitBreaker(
			ClosedRoute closedRoute, 
			OpenRoute openRoute, 
			RouterCompletionCallback callback ) {
		
		LOGGER.info("Current CB State before switch: " + StateProcessor.getStateProcessor().getCBState());
		
		switch(StateProcessor.getStateProcessor().getCBState()) {
			case CLOSED: {
				LOGGER.info("In CLOSED case");
				closedRoute.getChain().process(callback::success, (error, previous) -> {
					StateProcessor.getStateProcessor().incrementErrorCount();
					StateProcessor.getStateProcessor().checkErrorThreashold();
					LOGGER.info("ERROR IN CLOSED ROUTE");
					callback.error(error);
				});
				break;
			}
			case OPEN: {
				LOGGER.info("In OPEN case");
				openRoute.getChain().process(callback::success, (e, r) -> callback.error(e));
				break;
			}
			default: {
				LOGGER.info("In DEFAULT case");
				callback.success(Result.builder().build());
				LOGGER.info("Default state executed");
			}
				
		}

	}
}
