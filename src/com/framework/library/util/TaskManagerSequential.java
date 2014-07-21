package com.framework.library.util;

import java.util.ArrayList;

public class TaskManagerSequential {

	public static final int MAX_CONNECTIONS = 1;

	private ArrayList<Runnable> active = new ArrayList<Runnable>();
	private ArrayList<Runnable> queue = new ArrayList<Runnable>();

	private static TaskManagerSequential instance;

	public static TaskManagerSequential getInstance() {
		if (instance == null) {
			instance = new TaskManagerSequential();
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

	private void startNext() {
		if (!queue.isEmpty()) {
			Runnable next = queue.get(0);
			queue.remove(0);
			active.add(next);

			Thread thread = new Thread(next);
			thread.start();
		}
	}

	public void didComplete(Runnable runnable) {
		active.remove(runnable);
		startNext();
	}
}