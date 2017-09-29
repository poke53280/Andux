
ANDUX - HOWTO 

Andux version 0.4 beta

java com.malavida.Andux <port> 
	- port: Telnet port. 


-----------------------------------------------------------------------------

COMMAND PREFIXES 
a - Andux System Commands
e - Andux Engine Commands
f - Andux Focus Commands
p - Andux Net Commands


-----------------------------------------------------------------------------

SYSTEM COMMANDS

exit - log out
a shutdown - shuts down the system
a period <time> - sets inner loop time. allowed time: 10 and above. (milliseconds)
a uptime - shows system stats


a show <x> <y> - displays graphics x,y in size, upper left corner is at (0,0)
a hide - removes graphics


a control <id> - controls item <id>. If item <id> does not exist. no item will be controlled.
finger - shows user sessions
help - shows some other commands for configuring the shell.


a savescreen <filename> - Prints information about items currently on screen. (XML).


a open <filename> - Opens command file and, if auto is set, executes it command by command.

a auto <on|off> - Auto on executes all commands in a file when the file is opened.

a step - Executes next command line in already 'a open'ed command file. auto must be
set to 'off' for 'a step' to work. 

------------------------------------------------------------------------------

ENGINE COMMANDS


e start - starts engine. (Engine draws nothing unless started.)
e stop - stops engine, freezing positions and speed.

e status - shows engine status. ticks = total number of iterations since first started.
				items = number of items in engine.

e load <item_name> - loads an item from offline database into engine.
Some known names (case-sensitive): 'Anders', 'Magnus', 'Tree'.

e get <itemID> - displays status for an item in engine. 

e fill - loads a lot of items in one go.

e say <itemID> <blabla> - sets text for item <itemID>.


--------------------------------------------------------------------------------

DISPLAY COMMANDS

d status - Not in use.

--------------------------------------------------------------------------------

FOCUS COMMANDS

f fix <x> <y> - set upper left coner to coordinate (x,y)

f fix <id> - have window follow item <id>


--------------------------------------------------------------------------------

NET COMMANDS

p status - displays status of network nodes, if any.

p connect <port> <hostname>. Binds this machine to local port <port>, and 
sets up local name <hostname>, which can be freely picked. port and hostname
can not be changed. 

p contact <ip> <port>. Contacts a remote logical machine (residing on local or remote physical computer). 
<ip> is formatted as %d.%d.%d.%d or host.domain.com.

p own <proxyID> <localID> <remoteID>.
		For local proxyID (as shown with p status): Set item ID <localID> locally owned, 
		and item ID <remoteID> assumed owned by the remote host. The local host will 
		send a lot more updates from items it owns. It will never send updated of
		item <remoteID> assuming that item is owned remotely. 


p freq <pID> <normal/1000> <urgent/1000> 
		Sets sending frequency from this machine to remote logical machine <pID>.
		Frequency arguments are parts pr. thousand ticks that an update should
		be sent. urgent is owned item, normal all other items. 
				

p streamlist <pID>
		List all classes uploaded by remote side of pID: class names and size in bytes.
		
p sendclass <pID> <classname>
		Send a class to remote side.

p runclass <pID> <classname>
		Run a class that remote side has already uploaded to this machine. 


----------------------------------------------------------------------------------





















