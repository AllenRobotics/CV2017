package org.usfirst.frc.team5417.cv2017;

/******************************************************************************
 *  Compilation:  javac Stopwatch.java
 *  Execution:    java Stopwatch n
 *  Dependencies: none
 *
 *  A utility class to measure the running time (wall clock) of a program.
 *
 *  % java8 Stopwatch 100000000
 *  6.666667e+11  0.5820 seconds
 *  6.666667e+11  8.4530 seconds
 *
 ******************************************************************************/

/**
 *  The {@code Stopwatch} data type is for measuring
 *  the time that elapses between the start and end of a
 *  programming task (wall-clock time).
 *
 *  See {@link StopwatchCPU} for a version that measures CPU time.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Bryan Taylor
 */


public class Stopwatch { 

    private long start = 0;
    private double totalSeconds = 0;

    public static Stopwatch startNew() {
    	Stopwatch s = new Stopwatch();
    	s.start();
    	return s;
    }
    
    /**
     * Initializes a new stopwatch.
     */
    public Stopwatch() {
    } 

    public void start() {
    	if (start == 0) {
    		start = System.nanoTime();
    	}
    }

    public void stop() {
    	if (start != 0) {
	    	totalSeconds += elapsedSeconds();
	    	start = 0;
    	}
    }
    
    public double getTotalSeconds() {
    	if (start != 0) {
    		return totalSeconds + elapsedSeconds();
    	}
    	else {
    		return totalSeconds;
    	}
    }
    
    /**
     * Returns the elapsed CPU time (in seconds) since the stopwatch was created.
     *
     * @return elapsed CPU time (in seconds) since the stopwatch was created
     */
    private double elapsedSeconds() {
        long now = System.nanoTime();
        return (now - start) / 1000000000.0;
    }
} 