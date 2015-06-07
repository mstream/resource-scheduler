package org.mstream.exercise.scheduler.strategy;

import org.mstream.exercise.scheduler.resource.Message;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;


public class MessagesGroupStrategyTest {

	private MessagesGroupStrategy instance = new MessagesGroupStrategy( );

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

}