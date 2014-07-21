package com.framework.library.connection;

import java.util.ArrayList;

import com.framework.library.util.Functions;

public class ConnectionManagerImages {

	public static final int MAX_CONNECTIONS = 3;

	private ArrayList<Runnable> active = new ArrayList<Runnable>();
	private ArrayList<Runnable> queue = new ArrayList<Runnable>();

	private static ConnectionManagerImages instance;

	public static ConnectionManagerImages getInstance() {
		if (instance == null) {
			instance = new ConnectionManagerImages();
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

					Thread thread = new Thread(next);
					thread.start();
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
			startNext();
		} catch (Exception e) {
			// Functions.log(e);
		}
	}
}
