# Los leJOS

## Setup

### Eclipse

[Download leJOS NXJ](http://www.lejos.org/nxj-downloads.php) and extract somewhere, for example to `~/lejos`. Add the following to `~/.bashrc` (replacing the directory where appropriate).

```bash
export NXJ_HOME=~/lejos
export PATH=$PATH:$NXJ_HOME/bin
```

Follow [this guide](http://www.lejos.org/nxt/nxj/tutorial/Preliminaries/UsingEclipse.htm) to set the leJOS NXJ plugin up in Eclipse, importing the projects contained in this repository to your workspace using File > Import, selecting General > Existing Projects Into Workspace, then choosing the directory you cloned this repository to as the Root Diectory.


### USB

Issue the following command.

```bash
cd $NXJ_HOME/build
```

Next, run the command `ant`. This will build the necessary library for accessing the NXT over USB.

### Bluetooth

First, copy the Bluetooth library on the DICE filesystem to your leJOS directory by running the following command.

```bash
cp /usr/lib64/libbluetooth.so.3 $NXJ_HOME/libbluetooth.so
```

Next, add the following to `~/.bashrc`

```bash
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$NXJ_HOME
```

To communicate over Bluetooth using Eclipse, it must be opened from the terminal using the command `eclipse &` or it won't be able to locate the Bluetooth library. Alternatively, run the ant build script for each project by right-clicking `build.xml` and selecting Run As > Ant Build. 
