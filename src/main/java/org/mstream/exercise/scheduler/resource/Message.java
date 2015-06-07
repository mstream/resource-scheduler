package org.mstream.exercise.scheduler.resource;

public interface Message<ID> {
	ID getId( );

	ID getGroupId( );

	void completed( );
}
