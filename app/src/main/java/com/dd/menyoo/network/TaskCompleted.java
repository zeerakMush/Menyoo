package com.dd.menyoo.network;

public interface TaskCompleted {
	void onTaskCompletedSuccessfully(Object obj);

	void onTaskFailed();
}
