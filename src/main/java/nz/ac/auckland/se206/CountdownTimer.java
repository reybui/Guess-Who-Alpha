package nz.ac.auckland.se206;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CountdownTimer {
  private static final int INITIAL_TIME = 120; // 2 minutes in seconds
  private static final int GUESSING_TIME = 10;
  private int remainingTime;
  private ScheduledExecutorService scheduler;
  private Runnable onTick;
  private Runnable onFinish;
  private boolean isGuessingState = false;

  public CountdownTimer() {
    remainingTime = INITIAL_TIME;
    scheduler = Executors.newScheduledThreadPool(1);
  }

  public void start() {
    scheduler.scheduleAtFixedRate(
        () -> {
          if (remainingTime > 0) {
            remainingTime--;
            if (onTick != null) {
              onTick.run();
            }
          } else {
            if (onFinish != null) {
              onFinish.run();
            }
            if (isGuessingState) {
              stop();
            } else {
              resetToGuessingTime();
            }
          }
        },
        0,
        1,
        TimeUnit.SECONDS);
  }

  public void setOnTick(Runnable onTick) {
    this.onTick = onTick;
  }

  public void setOnFinish(Runnable onFinish) {
    this.onFinish = onFinish;
  }

  public int getRemainingTime() {
    return remainingTime;
  }

  public void stop() {
    scheduler.shutdown();
  }

  public void resetToGuessingTime() {
    remainingTime = GUESSING_TIME;
    isGuessingState = true;
    if (scheduler.isShutdown()) {
      scheduler = Executors.newScheduledThreadPool(1);
      start();
    }
  }

  public void setGuessingState(boolean isGuessingState) {
    this.isGuessingState = isGuessingState;
  }
}
