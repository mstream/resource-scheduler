package org.mstream.exercise.scheduler.strategy;

import org.mstream.exercise.scheduler.resource.Message;


public interface PrioritizationStrategy {

	void enqueue( Message<?> message );

	Message dequeue( );

	boolean isQueueEmpty( );

}
