433.92-Raspberry-Pi
===================

This is the java GUI that can be used to control 433.92Mhz devices. It communicates with the 433-daemon from the `lirc` branch.
This is only a proof of concept because i'm not a GUI designer. If people want to rewrite it or start all over, feel free. 
The purpose of this program is just to show what the 433-daemon (and it's JSON api) is capable of.

To set the server and port, just start the `433-gui.jar` as follows:
```
C:\433-gui.jar 192.168.1.199 5000
```
The next time the `433-gui.jar` starts, it will use this value. To change the server / port, start the `433-gui` again with the right
