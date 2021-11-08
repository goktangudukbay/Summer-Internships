I worked on a software project for radars. The project is classified.
The project was done on VxWorks platform. VxWorks is a real time operating system
for developing embedded systems. My job was to move the project to a Visual Studio platform on Windows
to make the debugging easier. The company had an operating system abstraction
that they used for years. This abstraction is an API for operating system
concepts that works in POSIX, Windows and VxWorks. I used this API developed by the company
to move the project to Visual Studio. After doing the configurations and some conversions from VxWorks library to the company API
I faced with a problem. VxWorks had a message queue and the radar project was using it. The message queues in VxWorks and Linux systems were different compared to Windows.
Later, I did a message queue implementation in Windows using shared memory. I used a circular buffer logic to handle the message queue. I stored 2 indexes, front and back to 
manage the queue. I did two different implementations. In the first one I used a mutex lock and a semaphore. The mutex lock was used to access the shared memory in a synchronized
manner. In this implementation, read operation was blocking however if there was not any space in the queue users could not write their messages. The semahore was used to make the read 
operation blocking. In the second implementation I used 2 semaphores and a mutex. The second semahore was used make the write operation blocking. This problem was similar to the
Bounded Buffer problem we learned in CS 342. 