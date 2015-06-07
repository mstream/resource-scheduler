package org.mstream.exercise.scheduler.strategy;

import org.mstream.exercise.scheduler.resource.Message;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mstream.exercise.scheduler.mocks.MessageFixture.mockMessage;
import static org.testng.Assert.*;


public class FifoStrategyTest {

	private FifoStrategy instance;

	@BeforeMethod
	public void setUp( ) {
		instance = new FifoStrategy( );
	}

	@Test
	public void shouldPrioritizeQueuedMessages( ) throws Exception {
		List<Message<String>> messages = Arrays.asList(
				mockMessage( "message1", "group2" ),
				mockMessage( "message2", "group1" ),
				mockMessage( "message3", "group2" ),
				mockMessage( "message4", "group3" )
		);
		messages.stream( ).forEachOrdered( instance::enqueue );
		assertEquals( instance.dequeue( ).getId( ), messages.get( 0 ).getId( ) );
		assertEquals( instance.dequeue( ).getId( ), messages.get( 1 ).getId( ) );
		assertEquals( instance.dequeue( ).getId( ), messages.get( 2 ).getId( ) );
		assertEquals( instance.dequeue( ).getId( ), messages.get( 3 ).getId( ) );
	}

	@Test
	public void shouldBeEmptyAfterInitialization( ) throws Exception {
		assertTrue( instance.isQueueEmpty() );
	}

	@Test
	public void shouldNotBeEmptyAfterEnqueue( ) throws Exception {
		instance.enqueue( mockMessage( "msg", "grp" ) );
		assertFalse( instance.isQueueEmpty( ) );
	}

	@Test
	public void shouldBeEmptyAfterDequeOfAllMessages( ) throws Exception {
		instance.enqueue( mockMessage( "msg", "grp" ) );
		instance.dequeue();
		assertTrue( instance.isQueueEmpty( ) );
	}

}