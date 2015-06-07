package org.mstream.exercise.scheduler.mocks;

import org.mstream.exercise.scheduler.resource.Message;
import org.mstream.exercise.scheduler.strategy.PrioritizationStrategy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


public class FifoStrategy implements PrioritizationStrategy {

	private final Queue<Message> messageQueue = new LinkedList<>( );

	@Override public void enqueue( Message<?> message ) {
		messageQueue.offer( message );
	}

	@Override public Message dequeue( ) {
		return messageQueue.poll( );
	}

	@Override public boolean isQueueEmpty( ) {
		return messageQueue.isEmpty( );
	}

	@Override public void cancel( Object groupId ) {
		Iterator<Message> msgIt = messageQueue.iterator( );
		while ( msgIt.hasNext( ) ) {
			Message msg = msgIt.next( );
			if ( groupId.equals( msg.getGroupId( ) ) ) {
				msgIt.remove( );
			}
		}
	}
}
