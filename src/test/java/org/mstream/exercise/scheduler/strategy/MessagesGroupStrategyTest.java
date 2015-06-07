package org.mstream.exercise.scheduler.strategy;

import org.mstream.exercise.scheduler.resource.Message;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


public class MessagesGroupStrategyTest {

	private MessagesGroupStrategy instance;

	private static <T> Message<T> messageFixture( T id, T groupId ) {
		return new Message<T>( ) {
			@Override public T getId( ) {
				return id;
			}

			@Override public T getGroupId( ) {
				return groupId;
			}

			@Override public void completed( ) {
				return;
			}
		};
	}

	@BeforeMethod
	public void setup( ) {
		instance = new MessagesGroupStrategy( );
	}

	@Test
	public void shouldPrioritizeQueuedMessages( ) throws Exception {
		List<Message<String>> messages = Arrays.asList(
				messageFixture( "message1", "group2" ),
				messageFixture( "message2", "group1" ),
				messageFixture( "message3", "group2" ),
				messageFixture( "message4", "group3" )
		);
		messages.stream( ).forEachOrdered( instance::enqueue );
		assertEquals( instance.dequeue( ).getId( ), messages.get( 0 ).getId( ) );
		assertEquals( instance.dequeue( ).getId( ), messages.get( 2 ).getId( ) );
		assertEquals( instance.dequeue( ).getId( ), messages.get( 1 ).getId( ) );
		assertEquals( instance.dequeue( ).getId( ), messages.get( 3 ).getId( ) );
	}

	@Test
	public void shouldBeEmptyAfterInitialization( ) throws Exception {
		assertTrue( instance.isQueueEmpty() );
	}

	@Test
	public void shouldNotBeEmptyAfterEnqueue( ) throws Exception {
		instance.enqueue( messageFixture( "msg", "grp" ) );
		assertFalse( instance.isQueueEmpty( ) );
	}

	@Test
	public void shouldBeEmptyAfterDequeOfAllMessages( ) throws Exception {
		instance.enqueue( messageFixture( "msg", "grp" ) );
		instance.dequeue();
		assertTrue( instance.isQueueEmpty( ) );
	}

}