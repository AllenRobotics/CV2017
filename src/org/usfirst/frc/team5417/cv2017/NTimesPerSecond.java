package org.usfirst.frc.team5417.cv2017;

import java.util.PriorityQueue;

public class NTimesPerSecond {

	private double timesPerSecond;
	private double targetDelay;
	
	private Stopwatch stopwatch;
	private double lastExecSeconds;

	// for moving average
	private PriorityQueue<Double> executions = new PriorityQueue<>();
	private static final double movingAverageWindowSeconds = 5.0;
	
	public NTimesPerSecond(double timesPerSecond) {
		this.timesPerSecond = timesPerSecond;
		this.targetDelay = 1.0 / timesPerSecond;
	}
	
	public void start() {
		this.stopwatch = Stopwatch.startNew();
		this.lastExecSeconds = this.stopwatch.getTotalSeconds();
	}
	
	public double nextDelaySeconds() {
		double now = this.stopwatch.getTotalSeconds();
		executions.add(now);
		
		double diff = this.stopwatch.getTotalSeconds() - this.lastExecSeconds;
		
		if (diff < this.targetDelay) {
			// we have (this.targetDelay - diff) remaining amount to delay before the next execution
			return this.targetDelay - diff;
		}
		else {
			// we're hitting max cpu usage here
			return 0;
		}
	}
	
	public int nextDelayMs() {
		double delaySeconds = this.nextDelaySeconds();
		return (int)(delaySeconds * 1000);
	}
	
	public double fps() {
		double now = this.stopwatch.getTotalSeconds();
		while (this.executions.size() > 0 && now - this.executions.peek() >= movingAverageWindowSeconds) {
			this.executions.remove();
		}
		
		if (this.executions.size() > 0) {
			double oldestTime = this.executions.peek();
			double count = this.executions.size();
			return count / (this.stopwatch.getTotalSeconds() - oldestTime);
		}
		else {
			return 0;
		}
	}
	
}
