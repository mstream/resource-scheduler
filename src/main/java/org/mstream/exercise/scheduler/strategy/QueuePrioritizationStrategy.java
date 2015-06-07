package org.mstream.exercise.scheduler.strategy;

import org.mstream.exercise.scheduler.resource.Message;


public interface QueuePrioritizationStrategy {

	void enqueue( Message message );

	Message dequeue( );

	boolean isQueueEmpty( );

	void cancel( Object groupId );

}
