package de.tutter05.kuhfuss.utils;

public class Timer {

    private long lastMs;

    public Timer() {
        this.lastMs = System.currentTimeMillis();
    }

    /**
     * Runs the specified runnable when the delay is completed, then resets the timer
     * @param delay time to wait between actions (in milliseconds)
     * @param runnable runnable to run when delay elapsed
     */
    public void onTimeout(final long delay, final Runnable runnable) {
        final long currentMs = System.currentTimeMillis();
        if(currentMs-lastMs >= delay) {
            runnable.run();
            reset();
        }
    }

    /**
     * Resets the timer
     */
    private void reset() {
        lastMs = System.currentTimeMillis();
    }

}
