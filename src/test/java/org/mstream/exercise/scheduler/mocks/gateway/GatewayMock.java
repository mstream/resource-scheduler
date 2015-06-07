package org.mstream.exercise.scheduler.mocks.gateway;

import org.mstream.exercise.scheduler.resource.Gateway;
import org.mstream.exercise.scheduler.resource.Message;

import java.util.LinkedList;
import java.util.List;


public class GatewayMock implements Gateway {

	private final int resourcesNumber;
	private int availableResources;
	private List<Message> processingMessages = new LinkedList<>( );

	public GatewayMock( int resourcesNumber ) {
		this.resourcesNumber = resourcesNumber;
		this.availableResources = resourcesNumber;
	}

	@Override public void send( Message<?> message ) throws NotEnoughOfAvailableResourcesException {
		if ( availableResources == 0 ) {
			throw new NotEnoughOfAvailableResourcesException( );
		}
		processingMessages.add( message );
	}

	public void finishProcessing( ) {
		assert !processingMessages.isEmpty( );
		Message processedMessage = processingMessages.remove( 0 );
		processedMessage.completed( );
	}

	public int getResourcesNumber( ) {
		return resourcesNumber;
	}

	public List<Message> getProcessingMessages( ) {
		return processingMessages;
	}

}

