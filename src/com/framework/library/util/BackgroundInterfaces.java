package com.framework.library.util;

import com.framework.library.exception.BackgroundException;
import com.framework.library.model.BackgroundResponse;


public class BackgroundInterfaces {
	public interface BackgroundMode {
		@SuppressWarnings("rawtypes")
		public void runInBackground(final BackgroundListener listener, final String methodName, final Object object, final Object[] params, final Class[] classes);
		@SuppressWarnings("rawtypes")
		public void runInBackgroundSequential(final BackgroundListener listener, final String methodName, final Object object, final Object[] params, final Class[] classes);
		@SuppressWarnings("rawtypes")
		public void runInBackgroundSequential(final BackgroundListener listener, final String methodName, final Object object, final Object[] params, final Class[] classes, int priority);
	}
	
	public interface BackgroundListener {
		public void onBackgroundComplete(BackgroundResponse response);
		public void onBackgroundError(BackgroundException exception);
	}
}
