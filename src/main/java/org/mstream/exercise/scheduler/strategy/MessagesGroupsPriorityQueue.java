package org.mstream.exercise.scheduler.strategy;

import org.mstream.exercise.scheduler.resource.Message;

import java.util.*;
import java.util.stream.Collectors;


public class MessagesGroupsPriorityQueue implements QueuePrioritizationStrategy {

	private List groupsOrder = new ArrayList( );
	private Queue<Message> messageQueue;
	private Comparator<Message> comparator;

	public MessagesGroupsPriorityQueue( ) {
		comparator = ( m1, m2 ) -> Integer.compare(
				groupsOrder.indexOf( m1.getGroupId( ) ),
				groupsOrder.indexOf( m2.getGroupId( ) ) );
		messageQueue = new PriorityQueue<>( comparator );
	}

	@Override public void enqueue( Message message ) {
		Object groupId = message.getGroupId( );
		if ( !groupsOrder.contains( groupId ) ) {
			groupsOrder.add( groupId );
		}
		messageQueue.offer( message );
	}

	@Override public Message dequeue( ) {
		return messageQueue.poll( );
	}

	@Override public boolean isQueueEmpty( ) {
		return messageQueue.isEmpty( );
	}

	@Override public void cancel( Object groupId ) {
		if ( messageQueue == null || messageQueue.isEmpty( ) ) {
			return;
		}
		Queue<Message> newMessageQueue = new PriorityQueue<>( comparator );
		newMessageQueue.addAll( messageQueue.stream( )
						.filter( msg -> !groupId.equals( msg.getGroupId( ) ) )
						.collect( Collectors.toList( ) )
		);
		messageQueue = newMessageQueue;
	}

}
