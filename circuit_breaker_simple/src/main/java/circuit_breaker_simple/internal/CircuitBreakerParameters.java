package circuit_breaker_simple.internal;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

public class CircuitBreakerParameters {

	@Parameter
	@Optional(defaultValue="2")
	@DisplayName("Closed error threshold")
	private Integer closedErrorThreshold = 2;
	
	@Parameter
	@Optional(defaultValue="5000")
	@DisplayName("Closed refresh period")
	private Integer closedRefreshPeriod = 5000;
	
	@Parameter
	@Optional(defaultValue="5000")
	@DisplayName("Open period")
	private Integer openPeriod = 5000;
	
	public Integer getClosedErrorThreshold() {
		return closedErrorThreshold;
	}

	public void setClosedErrorThreshold(Integer closedErrorThreshold) {
		this.closedErrorThreshold = closedErrorThreshold;
	}

	public Integer getClosedRefreshPeriod() {
		return closedRefreshPeriod;
	}

	public void setClosedRefreshPeriod(Integer closedRefreshPeriod) {
		this.closedRefreshPeriod = closedRefreshPeriod;
	}

	public Integer getOpenPeriod() {
		return openPeriod;
	}

	public void setOpenPeriod(Integer openPeriod) {
		this.openPeriod = openPeriod;
	}
}


