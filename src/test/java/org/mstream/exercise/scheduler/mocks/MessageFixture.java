package org.mstream.exercise.scheduler.mocks;

import org.mstream.exercise.scheduler.resource.Message;


public class MessageFixture {

	public static <T> Message<T> mockMessage( T id, T groupId ) {
		return mockMessage( id, groupId, false );
	}

	public static <T> Message<T> mockTerminatingMessage( T id, T groupId ) {
		return mockMessage( id, groupId, true );
	}

	private static <T> Message<T> mockMessage( T id, T groupId, boolean terminating ) {
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

			@Override public boolean isTerminating( ) {
				return terminating;
			}
		};
	}
}
