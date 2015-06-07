package org.mstream.exercise.scheduler;

import org.mstream.exercise.scheduler.resource.Gateway;
import org.mstream.exercise.scheduler.resource.Message;
import org.mstream.exercise.scheduler.strategy.PrioritizationStrategy;

import java.util.HashSet;
import java.util.Set;


public class ResourceScheduler {

	private final Gateway gateway;
	private final PrioritizationStrategy prioritizationStrategy;
	private final Set terminatedGroups;
	private final Set canceledGroups;
	private int availableResources;

	public ResourceScheduler( Gateway gateway, int resourcesNumber, PrioritizationStrategy prioritizationStrategy ) {
		this.gateway = gateway;
		this.prioritizationStrategy = prioritizationStrategy;
		this.terminatedGroups = new HashSet<>( );
		this.canceledGroups = new HashSet<>( );
		this.availableResources = resourcesNumber;
	}

	public void schedule( Message<?> message ) {
		if ( message == null ) {
			throw new IllegalArgumentException( "message can't be null" );
		}
		if (message.getId() == null || message.getGroupId() == null) {
			throw new IllegalArgumentException( "message has to have id and groupId" );
		}
		checkTermination( message );
		if ( canceledGroups.contains( message.getGroupId( ) ) ) {
			return;
		}
		Message wrappedMessage = new MessageWrapper<>( message );
		prioritizationStrategy.enqueue( wrappedMessage );
		if ( availableResources > 0 ) {
			sendNextToGateway( );
			availableResources--;
		}
	}

	public void cancel(Object group) {
		if (group == null) {
			throw new IllegalArgumentException( "group can't be null" );
		}
		canceledGroups.add( group );
	}

	private void checkTermination( Message<?> message ) {
		if ( terminatedGroups.contains( message.getGroupId( ) ) ) {
			throw new IllegalStateException( "can't schedule message from terminated group: " + message.getId( ) );
		}
		if ( message.isTerminating( ) ) {
			terminatedGroups.add( message.getGroupId( ) );
		}
	}

	private void sendNextToGateway( ) {
		assert anyPendingMessages( );
		gateway.send( prioritizationStrategy.dequeue( ) );
	}

	private boolean anyPendingMessages( ) {
		return !prioritizationStrategy.isQueueEmpty( );
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
			ResourceScheduler.this.availableResources++;
			if ( anyPendingMessages( ) ) {
				sendNextToGateway( );
			}
			delegate.completed( );
		}

		@Override public boolean isTerminating( ) {
			return delegate.isTerminating( );
		}
	}

}
