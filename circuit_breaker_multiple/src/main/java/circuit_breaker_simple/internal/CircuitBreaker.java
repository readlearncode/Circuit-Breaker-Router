package circuit_breaker_simple.internal;

import java.time.Instant;

import java.io.Serializable;

public class CircuitBreaker implements Serializable {
	
	private static final long serialVersionUID = 830689931409214266L;

	public CircuitBreaker(String circuitBreakerId, Integer closedErrorThreshold, Integer closedRefreshPeriod, Integer openPeriod) {
		this.circuitBreakerId = circuitBreakerId;
		this.closedErrorThreshold = closedErrorThreshold;
		this.closedRefreshPeriod = closedRefreshPeriod;
		this.openPeriod = openPeriod;
		this.cbState = CBState.CLOSED;
		this.timeSinceEpoch = Instant.now().toEpochMilli();
		this.setErrorCount(0);
	}

	private String circuitBreakerId;
	
	private Integer closedErrorThreshold;
	
	private Integer closedRefreshPeriod;
	
	private Integer openPeriod;

	private Integer errorCount;
	
	private CBState cbState;

	private Long timeSinceEpoch;
	
	public String getCircuitBreakerId() {
		return circuitBreakerId;
	}

	public void setCircuitBreakerId(String circuitBreakerId) {
		this.circuitBreakerId = circuitBreakerId;
	}

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

	public CBState getCbState() {
		return cbState;
	}

	public void setCbState(CBState cbState) {
		this.cbState = cbState;
	}

	public Long getTimeSinceEpoch() {
		return timeSinceEpoch;
	}

	public void setTimeSinceEpoch(Long timeSinceEpoch) {
		this.timeSinceEpoch = timeSinceEpoch;
	}

	public Integer getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

	public void incrementErrorCount() {
		this.errorCount = ++this.errorCount;
	}

	@Override
	public String toString() {
		return "\n CircuitBreaker: \n [circuitBreakerId=" + circuitBreakerId + ", \n closedErrorThreshold=" + closedErrorThreshold
				+ ", \n closedRefreshPeriod=" + closedRefreshPeriod + ", \n openPeriod=" + openPeriod + ", \n errorCount="
				+ errorCount + ", \n cbState=" + cbState + ", \n timeSinceEpoch=" + timeSinceEpoch + "]";
	}
	
}
