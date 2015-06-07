package org.mstream.exercise.scheduler.mocks;

import org.mstream.exercise.scheduler.resource.Message;


public class MessageFixture {

	public static <T> Message<T> mockMessage( T id, T groupId ) {
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
}
