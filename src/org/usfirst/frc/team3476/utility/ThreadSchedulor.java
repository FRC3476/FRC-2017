package org.usfirst.frc.team3476.utility;

public class ThreadScheduler implements Runnable {
	
	private ArrayList<Threaded> scheduledTasks;
	private ArrayList<double> taskPeriods, taskTimes;
	private ArrayList<Future<?>> scheduledFutures;
	private ArrayList<ExecutorService> threadPools;
	
	ThreadScheduler () {
		scheduledTasks = new ArrayList<Threaded>();
		runningTasks = new ArrayList<Threaded>();
		taskPeriods = new ArrayList<double>():
		taskTimes = new ArrayList<double>();
		scheduledFutures = new ArrayList<Future<?>>();
		threadPools = new ArrayList<ExecutorService>();
		
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
						if(scheduledFutures.get(task).isDone){
							scheduledFutures.set(task, threadPools.get(task).submit(scheduledTasks.get(i)));
							taskTimes.set(task, System.nanoTime());
						}
					}
				}
			}
			Thread.yield();
		}
	}
	
	public void schedule(Threaded task, double period, ExecutorService threadPool) {
		synchronized(this) {
			scheduledTasks.add(task);
			taskPeriods.add(period);
			taskTimes.add(System.nanoTime());
			threadPools.add(threadPool);
		}
	}
	
	public void shutdown() {
		isRunning = false;		
	}	
}
