Instruction
===========

This is a Maven project.
To run tests please run
```
mvn clean test
```
from project root directory.
Commentary
==========

0. Firstly, I'm creating Gateway and Message interface as described in instruction.
I don't write implementation of these interfaces because I treat it as an API to the unknown component.
I only assume that message should additionally have ID and group ID.

0. Having on mind that I'm going to implement an interchangeable method of prioritization,
at the very beginning I create an interface which implementations will encapsulate prioritization strategy.

0. I'm creating the reference strategy based on incoming messages groups order.
It's backed with priority queue with custom comparator.
The comparator uses indexes of groups held in a list in order of they have they have been scheduled.

0. Now I'm creating a resource scheduler implementation.
It takes mentioned strategy in constructor along with number of external resources.
To track number of available resources, every time i send a message to gateway, I decrease a counter.
The question is - how can I know when a task is completed? It's not my code who decides.
All I know is the message complete() method is called then.
Therefore whenever I send a message to gateway, I'm wrapping it with decorator which holds a reference
to the scheduler and decreases counter when complete() method is called.
It invokes the wrapped message's complete() method too.
When the scheduler tries to send messages?
Whenever schedule is called or processing message is completed.

0. Now I'm implementing the termination functionality.
I'm using a HashSet which holds every terminated group and check on scheduling whether message
which belongs to this group has not been already terminated.
Throwing an exception then.

0. Next, I'm implementing cancellation.
I'm doing it in similar way, except that I ignore messages from cancelled groups
and I'm removing already queued messages from this group.

0. At the end I'm adding a thread safety.
I create three separate locks.
One for cancelling, one for terminating and one for queue manipulation.
First two assure that message won't be processed after its group cancellation.
The third one make sure that messages are not sent if no resource is available.
Additionally, it secures priority strategy implementation which does not have to be thread safe.
Now message can be scheduled and processed by many threads without side effects.


