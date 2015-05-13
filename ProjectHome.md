# Introduction #

`Thread Dump Sampler` is an asynchronous thread stack miner for finding Java performance issues.
It is a light-weight and non-intrusive tool; no agent lib or code injection, it uses only thread dumps!

# How does it work? #

As HProf CPU profiling, `TDSampler` parses stacks to detect the most frequently active methods. We can expect to find easily the application hotspots by analyzing the top N most active methods.

It works well to identify issue on a running system with high and consistent load for some period of time (more we have stacks, more the statistics are accurate).

If you have a performance issue that you can easily reproduce without load, you should probably prefer using a profiler rather than using a sampling method.

# Releases #

  * [TDSampler-1.7-bin.zip](http://code.google.com/p/tdsampler/downloads/detail?name=TDSampler-1.7-bin.zip)

# Compatibility #

Tested with JDK 6 on Linux RHE 5.7.

# Getting started #

  * Collect regular thread dumps on your running application using `jstack` command line (or `kill -3`).
  * Below is an example of script for Linux:
```
#!/bin/bash

mkdir -p /tmp/thread_dumps
chmod -R 775 /tmp/thread_dumps
while : ; do
FILE=/tmp/thread_dumps/td.$(date +"%d%m%Y_%H_%M_%S").txt
jstack $( ps aux |grep java | grep -v grep |awk '{print $2}') > $FILE
sleep 60;
done
```
  * Below is an example of script for Windows:
```
@echo off
setlocal

:loop
set timehour=%time:~0,2%
C:\"Program Files"\Java\jdk1.6.0_20\bin\jstack %1 > thread_dump-%date:~-4,4%%date:~-10,2%%date:~-7,2%-%timehour: =0%%time:~3,2%.txt
ping -n 60 127.0.0.1>NUL
goto loop
```
Note, thread dumps should not contain bci info (do not generate with root).
  * `wget http://tdsampler.googlecode.com/files/TDSampler-<major>.<inc>-bin.zip`
  * `unzip TDSampler-<major>.<inc>-bin.zip`
  * `cd TDSampler-<major>.<inc>/bin`
  * `chmod 775 start.sh`
  * `/start.sh /tmp/thread_dumps`

# Configuration #

  * Change `conf/conf.properties` to configure the hotspot report:

|**Key**|**Default**|**Comments**|
|:------|:----------|:-----------|
|displayThreadStateReport|false|If 'true', display thread state report.|
|countDuplicateMethods|false|if 'true', a method that appears twice in a stack is counted twice in the report.|
|includeListIOWait|None|List of strings separated by a comma to indicate methods that are blocked by I/O wait. These methods don't consume CPU instead they perform network calls, involve OS background activity or delegate to other applications such as database engines (wall-clock time and not CPU time).|
|includeListThreadName|All|List of strings separated by a comma to include stacks with these thread names.|
|includeListThreadState|All|List of strings separated by a comma to include stacks with these thread status (RUNNABLE, IOWAIT, WAITING, TIMED\_WAITING or BLOCKED).|
|includeListThread|All|List of strings separated by a comma to include stacks that are containing these method names.|
|excludeListThread|None|List of strings separated by a comma to exclude stacks that are containing these method names.|
|includeListMethod|All|List of strings separated by a comma to include methods with these method names.|
|excludeListMethod|None|List of strings separated by a comma to exclude methods with these method names.|

  * Configuration example for report of top CPU-consumer methods (runnable state but not blocked on I/O wait methods) of TP-Processor threads executing Ehcache code:

```
displayThreadStateReport=false
countDuplicateMethods=false
includeListIOWait=java.net.SocketInputStream.socketRead0(Native Method),java.net.PlainSocketImpl.socketAccept(Native Method),java.net.PlainDatagramSocketImpl.receive0(Native Method)
includeListThreadName=TP-Processor
includeListThreadState=RUNNABLE
includeListThread=net.sf.ehcache
excludeListThread=
includeListMethod=
excludeListMethod=
```

  * Configuration example for report of top time-consumer methods of Ehcache library executed by TP-Processor threads:

```
displayThreadStateReport=false
countDuplicateMethods=false
includeListIOWait=java.net.SocketInputStream.socketRead0(Native Method),java.net.PlainSocketImpl.socketAccept(Native Method),java.net.PlainDatagramSocketImpl.receive0(Native Method)
includeListThreadName=TP-Processor
includeListThreadState=
includeListThread=
excludeListThread=
includeListMethod=net.sf.ehcache
excludeListMethod=
```