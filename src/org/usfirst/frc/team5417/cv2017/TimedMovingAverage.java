package org.usfirst.frc.team5417.cv2017;

import java.util.PriorityQueue;

import org.usfirst.frc.team5417.cv2017.customops.Pixel;

public class TimedMovingAverage {

	private class TimeAndValue implements Comparable<TimeAndValue>
	{
		public double time;
		public double value;
		
		@Override
		public int compareTo(TimeAndValue o) {
			// TODO Auto-generated method stub
			return this.time < o.time ? -1 :
				this.time > o.time ? 1 :
					0;
		}
	}
	
	private Stopwatch stopwatch = Stopwatch.startNew();
	private PriorityQueue<TimeAndValue> dataPoints = new PriorityQueue<>();
	private double movingAverageWindowSeconds;

	public TimedMovingAverage(double movingAverageWindowSeconds) {
		this.movingAverageWindowSeconds = movingAverageWindowSeconds;
	}
	
	public void recordDataPoint(double dataPoint) {
		double now = stopwatch.getTotalSeconds();
		
		TimeAndValue t = new TimeAndValue();
		t.time = now;
		t.value = dataPoint;
		dataPoints.add(t);
	}
	
	public double average() {
		double now = this.stopwatch.getTotalSeconds();
		while (this.dataPoints.size() > 0 && now - this.dataPoints.peek().time >= movingAverageWindowSeconds) {
			this.dataPoints.remove();
		}
		
		if (this.dataPoints.size() > 0) {
			double sum = 0;
			for (TimeAndValue t : this.dataPoints)
			{
				sum += t.value;
			}
			double count = this.dataPoints.size();
			return sum / count;
		}
		else {
			return 0;
		}
	}
}
