package org.mstream.exercise.scheduler.resource;

public interface Message<ID> {
	ID getGroupId();
	void completed();
}
