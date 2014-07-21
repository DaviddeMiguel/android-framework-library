package com.framework.library.connection;

import java.util.ArrayList;

import com.framework.library.util.Functions;

public class ConnectionManagerImagesReusingThreads {

	public static final int MAX_CONNECTIONS = 5;

	private ArrayList<Runnable> active = new ArrayList<Runnable>();
	private ArrayList<Runnable> queue = new ArrayList<Runnable>();
	private ArrayList<Worker> workerActive = new ArrayList<Worker>(
			MAX_CONNECTIONS);
	private ArrayList<Worker> workerWaiting = new ArrayList<Worker>(
			MAX_CONNECTIONS);

	private ConnectionManagerImagesReusingThreads() {
		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			Worker worker = new Worker();
			workerWaiting.add(worker);
			worker.start();
		}
	}

	private static ConnectionManagerImagesReusingThreads instance;

	public static ConnectionManagerImagesReusingThreads getInstance() {
		if (instance == null) {
			instance = new ConnectionManagerImagesReusingThreads();
		}

		return instance;
	}

	public void push(Runnable runnable) {
		queue.add(runnable);
		if (active.size() < MAX_CONNECTIONS) {
			startNext();
		}
	}

	public void push(Runnable runnable, int priority) {
		if (priority < queue.size()) {
			queue.add(priority, runnable);
		} else {
			queue.add(0, runnable);
		}
		
		if (active.size() < MAX_CONNECTIONS) {
			startNext();
		}
	}

	@SuppressWarnings("rawtypes")
	private void startNext() {
		try {
			if (!queue.isEmpty()) {
				HttpConnection next = (HttpConnection) queue.get(0);
				queue.remove(0);
				if (!next.isCancelled()) {
					active.add(next);

					Worker worker = workerWaiting.get(0);
					workerWaiting.remove(worker);
					workerActive.add(worker);
					worker.setRunnable(next);
					workerActive.notify();
				} else {
					startNext();
				}
			}
		} catch (Exception e) {
			Functions.log(e);
		}
	}

	public void didComplete(Runnable runnable) {
		try {
			active.remove(runnable);
		} catch (Exception e) {
			// Functions.log(e);
		}
	}

	private class Worker extends Thread {

		private Runnable runnable;

		public Runnable getRunnable() {
			return runnable;
		}

		public void setRunnable(Runnable runnable) {
			this.runnable = runnable;
		}

		@Override
		public void run() {
			while (true) {
				try {
					if (getRunnable() == null) {
						try {
							workerActive.wait();

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						getRunnable().run();
						workerActive.remove(this);
						workerWaiting.add(this);
						setRunnable(null);
						startNext();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}
}
