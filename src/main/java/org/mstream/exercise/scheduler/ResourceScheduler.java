package org.mstream.exercise.scheduler;

import org.mstream.exercise.scheduler.resource.Gateway;
import org.mstream.exercise.scheduler.resource.Message;
import org.mstream.exercise.scheduler.strategy.PrioritizationStrategy;


public class ResourceScheduler {

	private final Gateway gateway;
	private final PrioritizationStrategy prioritizationStrategy;
	private int availableResources;

	public ResourceScheduler( Gateway gateway, int resourcesNumber, PrioritizationStrategy prioritizationStrategy ) {
		this.gateway = gateway;
		this.availableResources = resourcesNumber;
		this.prioritizationStrategy = prioritizationStrategy;
	}

	public void schedule( Message<?> message ) {
		Message wrappedMessage = new MessageWrapper<>( message );
		prioritizationStrategy.enqueue( wrappedMessage );
		if ( availableResources > 0 ) {
			sendNextToGateway( );
			availableResources--;
		}
	}

	private void sendNextToGateway( ) {
		assert anyPandingMessages( );
		gateway.send( prioritizationStrategy.dequeue( ) );
	}

	private boolean anyPandingMessages( ) {
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
			if ( anyPandingMessages( ) ) {
				sendNextToGateway( );
			}
			delegate.completed( );
		}
	}

}
