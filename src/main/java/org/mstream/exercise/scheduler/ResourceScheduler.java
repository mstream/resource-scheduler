package org.mstream.exercise.scheduler;

import org.mstream.exercise.scheduler.resource.Gateway;
import org.mstream.exercise.scheduler.resource.Message;
import org.mstream.exercise.scheduler.strategy.QueuePrioritizationStrategy;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ResourceScheduler {

	private final Gateway gateway;
	private final QueuePrioritizationStrategy queuePrioritizationStrategy;
	private final Set terminatedGroups;
	private final Set canceledGroups;

	private final Lock cancellationLock = new ReentrantLock( );
	private final Lock terminationLock = new ReentrantLock( );
	private final Lock queueLock = new ReentrantLock( );

	private int availableResources;

	public ResourceScheduler( Gateway gateway, int resourcesNumber, QueuePrioritizationStrategy queuePrioritizationStrategy ) {
		this.gateway = gateway;
		this.queuePrioritizationStrategy = queuePrioritizationStrategy;
		this.terminatedGroups = new HashSet<>( );
		this.canceledGroups = new HashSet<>( );
		this.availableResources = resourcesNumber;
	}

	public void schedule( Message message ) {
		terminationLock.lock( );
		if ( message == null ) {
			throw new IllegalArgumentException( "message can't be null" );
		}
		if ( message.getId( ) == null || message.getGroupId( ) == null ) {
			throw new IllegalArgumentException( "message has to have id and groupId" );
		}
		checkTermination( message );
		terminationLock.unlock( );
		cancellationLock.lock( );
		if ( canceledGroups.contains( message.getGroupId( ) ) ) {
			return;
		}
		cancellationLock.unlock( );
		Message wrappedMessage = new MessageWrapper<>( message );
		queueLock.lock( );
		queuePrioritizationStrategy.enqueue( wrappedMessage );
		if ( availableResources > 0 ) {
			sendNextToGateway( );
			availableResources--;
		}
		queueLock.unlock( );
	}

	public void cancel( Object group ) {
		cancellationLock.lock( );
		if ( group == null ) {
			throw new IllegalArgumentException( "group can't be null" );
		}
		canceledGroups.add( group );
		cancellationLock.unlock( );
	}

	private void checkTermination( Message message ) {
		if ( terminatedGroups.contains( message.getGroupId( ) ) ) {
			throw new IllegalStateException( "can't schedule message from terminated group: " + message.getId( ) );
		}
		if ( message.isTerminating( ) ) {
			terminatedGroups.add( message.getGroupId( ) );
		}
	}

	private void sendNextToGateway( ) {
		assert anyPendingMessages( );
		gateway.send( queuePrioritizationStrategy.dequeue( ) );
	}

	private boolean anyPendingMessages( ) {
		return !queuePrioritizationStrategy.isQueueEmpty( );
	}

	private class MessageWrapper<T> implements Message<T> {

		private final Message<T> delegate;

		private MessageWrapper( Message<T> delegate ) {
			this.delegate = delegate;
		}

		@Override public T getId( ) {
			return delegate.getId( );
		}

		@Override public T getGroupId( ) {
			return delegate.getGroupId( );
		}

		@Override public void completed( ) {
			queueLock.lock( );
			ResourceScheduler.this.availableResources++;
			if ( anyPendingMessages( ) ) {
				sendNextToGateway( );
			}
			queueLock.unlock( );
			delegate.completed( );
		}

		@Override public boolean isTerminating( ) {
			return delegate.isTerminating( );
		}
	}

}
