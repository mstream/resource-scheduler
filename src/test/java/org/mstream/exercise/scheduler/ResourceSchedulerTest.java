package org.mstream.exercise.scheduler;

import org.mstream.exercise.scheduler.mocks.FifoStrategyQueue;
import org.mstream.exercise.scheduler.mocks.gateway.GatewayMock;
import org.mstream.exercise.scheduler.mocks.gateway.NotEnoughOfAvailableResourcesException;
import org.mstream.exercise.scheduler.resource.Message;
import org.testng.annotations.Test;

import static org.mstream.exercise.scheduler.mocks.MessageFixture.mockMessage;
import static org.mstream.exercise.scheduler.mocks.MessageFixture.mockTerminatingMessage;
import static org.testng.Assert.*;


public class ResourceSchedulerTest {

	private ResourceScheduler instance;

	@Test( expectedExceptions = { IllegalArgumentException.class } )
	public void shouldThrowExceptionWhenNullMessageScheduled( ) throws Exception {
		GatewayMock gatewayMock = new GatewayMock( 1 );
		instance = new ResourceScheduler( gatewayMock, gatewayMock.getResourcesNumber( ), new FifoStrategyQueue( ) );
		try {
			instance.schedule( null );
		} catch ( NotEnoughOfAvailableResourcesException e ) {
			fail( "scheduler sent too many messages to gateway" );
		}
	}

	@Test
	public void shouldForwardOneMessageToSingleResource( ) throws Exception {
		GatewayMock gatewayMock = new GatewayMock( 1 );
		instance = new ResourceScheduler( gatewayMock, gatewayMock.getResourcesNumber( ), new FifoStrategyQueue( ) );
		Message<String> message = mockMessage( "msg", "grp" );
		try {
			instance.schedule( message );
		} catch ( NotEnoughOfAvailableResourcesException e ) {
			fail( "scheduler sent too many messages to gateway" );
		}
		assertEquals( gatewayMock.getProcessingMessages( ).size( ), 1 );
		assertEquals( gatewayMock.getProcessingMessages( ).get( 0 ).getId( ), message.getId( ) );
	}

	@Test
	public void shouldForwardTwoMessagesToTwoResources( ) throws Exception {
		GatewayMock gatewayMock = new GatewayMock( 2 );
		instance = new ResourceScheduler( gatewayMock, gatewayMock.getResourcesNumber( ), new FifoStrategyQueue( ) );
		Message<String> messageA = mockMessage( "msgA", "grp" );
		Message<String> messageB = mockMessage( "msgB", "grp" );
		try {
			instance.schedule( messageA );
			instance.schedule( messageB );
		} catch ( NotEnoughOfAvailableResourcesException e ) {
			fail( "scheduler sent too many messages to gateway" );
		}
		assertEquals( gatewayMock.getProcessingMessages( ).size( ), 2 );
		assertEquals( gatewayMock.getProcessingMessages( ).get( 0 ).getId( ), messageA.getId( ) );
		assertEquals( gatewayMock.getProcessingMessages( ).get( 1 ).getId( ), messageB.getId( ) );
	}

	@Test
	public void shouldQueueOneMessageWhenTwoMessagesSentToSingleResource( ) throws Exception {
		GatewayMock gatewayMock = new GatewayMock( 1 );
		instance = new ResourceScheduler( gatewayMock, gatewayMock.getResourcesNumber( ), new FifoStrategyQueue( ) );
		Message<String> messageA = mockMessage( "msgA", "grp" );
		Message<String> messageB = mockMessage( "msgB", "grp" );
		try {
			instance.schedule( messageA );
			instance.schedule( messageB );
		} catch ( NotEnoughOfAvailableResourcesException e ) {
			fail( "scheduler sent too many messages to gateway" );
		}
		assertEquals( gatewayMock.getProcessingMessages( ).size( ), 1 );
		assertEquals( gatewayMock.getProcessingMessages( ).get( 0 ).getId( ), messageA.getId( ) );
	}

	@Test
	public void shouldRespondWhenMessageProcessingCompletes( ) throws Exception {
		GatewayMock gatewayMock = new GatewayMock( 1 );
		instance = new ResourceScheduler( gatewayMock, gatewayMock.getResourcesNumber( ), new FifoStrategyQueue( ) );
		Message<String> messageA = mockMessage( "msgA", "grp" );
		Message<String> messageB = mockMessage( "msgB", "grp" );
		try {
			instance.schedule( messageA );
			instance.schedule( messageB );
		} catch ( NotEnoughOfAvailableResourcesException e ) {
			fail( "scheduler sent too many messages to gateway" );
		}
		gatewayMock.finishProcessing( );
		assertEquals( gatewayMock.getProcessingMessages( ).size( ), 1 );
		assertEquals( gatewayMock.getProcessingMessages( ).get( 0 ).getId( ), messageB.getId( ) );
	}

	@Test( expectedExceptions = { IllegalStateException.class } )
	public void shouldThrowExceptionWhenMessageFromTerminatedGroupSent( ) throws Exception {
		GatewayMock gatewayMock = new GatewayMock( 1 );
		instance = new ResourceScheduler( gatewayMock, gatewayMock.getResourcesNumber( ), new FifoStrategyQueue( ) );
		Message<String> terminatingMessage = mockTerminatingMessage( "msgA", "grp" );
		Message<String> message = mockMessage( "msgB", "grp" );
		try {
			instance.schedule( terminatingMessage );
			instance.schedule( message );
		} catch ( NotEnoughOfAvailableResourcesException e ) {
			fail( "scheduler sent too many messages to gateway" );
		}
	}

	@Test
	public void shouldNotForwardFurtherMessagesFromCanceledGroup( ) throws Exception {
		GatewayMock gatewayMock = new GatewayMock( 1 );
		instance = new ResourceScheduler( gatewayMock, gatewayMock.getResourcesNumber( ), new FifoStrategyQueue( ) );
		Message<String> message = mockMessage( "msg", "grp" );
		instance.cancel( "grp" );
		try {
			instance.schedule( message );
		} catch ( NotEnoughOfAvailableResourcesException e ) {
			fail( "scheduler sent too many messages to gateway" );
		}
		assertTrue( gatewayMock.getProcessingMessages( ).isEmpty( ) );
	}
}