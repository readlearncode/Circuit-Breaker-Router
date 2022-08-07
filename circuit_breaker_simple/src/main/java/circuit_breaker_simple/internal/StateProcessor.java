package circuit_breaker_simple.internal;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.mule.runtime.api.store.ObjectStore;
import org.mule.runtime.api.store.ObjectStoreException;
import org.mule.runtime.api.store.ObjectStoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateProcessor {
	private final static Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerRouter.class);

	private static StateProcessor stateProcessor;

	public static synchronized void setStateProcessor(ObjectStoreManager osm, CircuitBreakerParameters cBconfig) {
		LOGGER.info("Creating new StateProcessor and setting as static value to StateProcessor");
		if (stateProcessor == null) {
			stateProcessor = new StateProcessor(osm, cBconfig);
		}
	}

	public static StateProcessor getStateProcessor() {
		LOGGER.info("Getting StateProcessor");
		return stateProcessor;
	}

	private ObjectStoreManager osm;
	private CBState state = CBState.CLOSED;
	private ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);
	private ScheduledFuture<?> closedRefreshSchedular;
	private ScheduledFuture<?> openPeriodSchedular;
	private Integer closedErrorThreshold;

	private Runnable closedRefreshTask = () -> {
		LOGGER.info("Getting StateProcessor: closedRefreshTask");
		StateProcessor.getStateProcessor().refreshClosed();
	};

	private Runnable openPeriodTask = () -> {
		LOGGER.info("Getting StateProcessor: openPeriodTask");
		StateProcessor.getStateProcessor().openPeriod();
	};

	private StateProcessor(ObjectStoreManager osm, CircuitBreakerParameters cBconfig) {
		LOGGER.info("Construtor of StateProcessor");
		this.osm = osm;
		this.closedErrorThreshold = cBconfig.getClosedErrorThreshold();
		this.state = CBState.CLOSED;

		LOGGER.info("Launching scheduled tasks");
		closedRefreshSchedular = ses.scheduleAtFixedRate(closedRefreshTask, 0, cBconfig.getClosedRefreshPeriod(), TimeUnit.MILLISECONDS);
		openPeriodSchedular = ses.scheduleAtFixedRate(openPeriodTask, 0, cBconfig.getOpenPeriod(), TimeUnit.MILLISECONDS);
	}

	public Integer getClosedErrorThreshold() {
		return closedErrorThreshold;
	}
	
	public ObjectStoreManager getOsm() {
		return osm;
	}

	public void setOsm(ObjectStoreManager osm) {
		this.osm = osm;
	}
	
	public CBState getCBState() {
		return this.state;
	}

	public void setCBState(CBState state) {
		this.state = state;
	}

	public void incrementErrorCount() {
		ObjectStore<Integer> os = osm.getDefaultPartition();
		try {
			int errorCount = 0;
			if (os.contains("errorCount")) {
				errorCount = os.remove("errorCount").intValue();
			}
			LOGGER.info("Current error count: " + errorCount);
			int newErrorCount = ++errorCount;
			os.store("errorCount", newErrorCount);
			LOGGER.info("New error count: " + newErrorCount);
		} catch (ObjectStoreException e) {
			e.printStackTrace();
		}

	}

	// reset the error count to zero
	public void refreshClosed() {
		try {
			ObjectStore<Integer> os = this.getOsm().getDefaultPartition();
			if (os.contains("errorCount")) {
				int errorCount = os.remove("errorCount").intValue();
				LOGGER.info("Error count reset to zero. New value is: " + errorCount);
			}
		} catch (ObjectStoreException e) {
			e.printStackTrace();
		}

	}

	// close circuit
	public void openPeriod() {
		setCBState(CBState.CLOSED);
	}

	public void checkErrorThreashold() {
		LOGGER.info("Checking error threashold");
		try {
			ObjectStore<Integer> os = this.getOsm().getDefaultPartition();
			if (os.contains("errorCount")
					&& os.retrieve("errorCount").intValue() > this.getClosedErrorThreshold()) {

				LOGGER.info("Error threashold breached");
				int errorCount = os.remove("errorCount").intValue();
				LOGGER.info("Reset error count and old error value: " + errorCount);
				this.setCBState(CBState.OPEN);
				LOGGER.info("Circuit Status: " + this.getCBState().toString());
			} else {
				LOGGER.info("Error threashold NOT breached");
			}
		} catch (ObjectStoreException e) {
			e.printStackTrace();
		}

	}

}
