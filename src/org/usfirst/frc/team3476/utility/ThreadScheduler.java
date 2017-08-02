package org.usfirst.frc.team3476.utility;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadScheduler implements Runnable {
	
	private ArrayList<Threaded> scheduledTasks;
	private ArrayList<Double> taskPeriods, taskTimes;
	private ArrayList<Future<?>> scheduledFutures;
	private ArrayList<ExecutorService> threadPools;
	private volatile boolean isRunning;
	
	public ThreadScheduler () {
		scheduledTasks = new ArrayList<Threaded>();
		taskPeriods = new ArrayList<Double>();
		taskTimes = new ArrayList<Double>();
		scheduledFutures = new ArrayList<Future<?>>();
		threadPools = new ArrayList<ExecutorService>();
		isRunning = true;
		
		ExecutorService schedulingThread = Executors.newSingleThreadExecutor();
		schedulingThread.execute(this);	
	}
	
	@Override
	public void run(){
		while(isRunning) {
			synchronized(this){
				for(int task = 0; task < scheduledTasks.size(); task++) {
					double duration = System.nanoTime() - taskTimes.get(task);
					if(duration > taskPeriods.get(task)) {
						if(scheduledFutures.get(task).isDone()){
							scheduledFutures.set(task, threadPools.get(task).submit(scheduledTasks.get(task)));
							taskTimes.set(task, (double) System.nanoTime());
						}
					}
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void schedule(Threaded task, double period, ExecutorService threadPool) {
		synchronized(this) {
			scheduledTasks.add(task);
			scheduledFutures.add(threadPool.submit(task));
			taskPeriods.add(period);
			taskTimes.add((double) System.nanoTime());
			threadPools.add(threadPool);
		}
	}
	
	public void shutdown() {
		isRunning = false;		
	}	
}
