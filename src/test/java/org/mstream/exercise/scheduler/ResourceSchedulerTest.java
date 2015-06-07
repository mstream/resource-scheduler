package org.mstream.exercise.scheduler;

import org.mstream.exercise.scheduler.mocks.FifoStrategy;
import org.mstream.exercise.scheduler.mocks.MessageFixture;
import org.mstream.exercise.scheduler.mocks.gateway.GatewayMock;
import org.mstream.exercise.scheduler.mocks.gateway.NotEnoughOfAvailableResourcesException;
import org.mstream.exercise.scheduler.resource.Gateway;
import org.mstream.exercise.scheduler.resource.Message;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mstream.exercise.scheduler.mocks.MessageFixture.*;
import static org.testng.Assert.*;


public class ResourceSchedulerTest {

	private ResourceScheduler instance;

	@Test
	public void shouldForwardOneMessageToSingleResource( ) throws Exception {
		GatewayMock gatewayMock = new GatewayMock( 1 );
		instance = new ResourceScheduler( gatewayMock, gatewayMock.getResourcesNumber(), new FifoStrategy() );
		Message<String> message = mockMessage( "msg", "grp" );
		try {
			instance.schedule( message );
		} catch ( NotEnoughOfAvailableResourcesException e ) {
			fail( "scheduler sent too many messages to gateway" );
		}
		assertEquals( gatewayMock.getProcessingMessages().size(), 1 );
		assertEquals( gatewayMock.getProcessingMessages().get( 0 ).getId(), message.getId() );
	}

	@Test
	public void shouldForwardTwoMessagesToTwoResources( ) throws Exception {
		GatewayMock gatewayMock = new GatewayMock( 2 );
		instance = new ResourceScheduler( gatewayMock, gatewayMock.getResourcesNumber(), new FifoStrategy() );
		Message<String> messageA = mockMessage( "msgA", "grp" );
		Message<String> messageB = mockMessage( "msgB", "grp" );
		try {
			instance.schedule( messageA );
			instance.schedule( messageB );
		} catch ( NotEnoughOfAvailableResourcesException e ) {
			fail( "scheduler sent too many messages to gateway" );
		}
		assertEquals( gatewayMock.getProcessingMessages().size(), 2 );
		assertEquals( gatewayMock.getProcessingMessages().get( 0 ).getId(), messageA.getId() );
		assertEquals( gatewayMock.getProcessingMessages().get( 1 ).getId(), messageB.getId() );
	}

	@Test
	public void shouldQueueOneMessageWhenTwoMessagesSentToSingleResource( ) throws Exception {
		GatewayMock gatewayMock = new GatewayMock( 1 );
		instance = new ResourceScheduler( gatewayMock, gatewayMock.getResourcesNumber(), new FifoStrategy() );
		Message<String> messageA = mockMessage( "msgA", "grp" );
		Message<String> messageB = mockMessage( "msgB", "grp" );
		try {
			instance.schedule( messageA );
			instance.schedule( messageB );
		} catch ( NotEnoughOfAvailableResourcesException e ) {
			fail( "scheduler sent too many messages to gateway" );
		}
		assertEquals( gatewayMock.getProcessingMessages( ).size( ), 1 );
		assertEquals( gatewayMock.getProcessingMessages().get( 0 ).getId(), messageA.getId() );
	}

	@Test
	public void shouldRespondWhenMessageProcessingCompletes( ) throws Exception {
		GatewayMock gatewayMock = new GatewayMock( 1 );
		instance = new ResourceScheduler( gatewayMock, gatewayMock.getResourcesNumber(), new FifoStrategy() );
		Message<String> messageA = mockMessage( "msgA", "grp" );
		Message<String> messageB = mockMessage( "msgB", "grp" );
		try {
			instance.schedule( messageA );
			instance.schedule( messageB );
		} catch ( NotEnoughOfAvailableResourcesException e ) {
			fail( "scheduler sent too many messages to gateway" );
		}
		gatewayMock.finishProcessing();
		assertEquals( gatewayMock.getProcessingMessages().size(), 1 );
		assertEquals( gatewayMock.getProcessingMessages().get( 0 ).getId(), messageB.getId() );
	}
}