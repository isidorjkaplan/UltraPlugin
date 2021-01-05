# UltraPlugin
## About this project
 While I was in Grade 11 and 12 I single-handidly created this codebase. It consists of a core library, reffered to as "UltraLib," which stores a set of useful libraries and types. I then used this library to create a large amount of independant modular plugins that share functionality through the core plugin. These can be found in the 'modules' and 'abandoned' folders. 

## What is a plugin?
To be clear for anyone not familiar with minecraft (anyone interested in hiring me for a job hopefully) a "plugin" is code that a "minecraft server" runs. In general when people play minecraft they join a set of independantly run worlds which are typically refferred to as a "server." As it so happens, a minecraft server is hosted on an actual server and when it is running people who run servers can inject code to modify the gameplay, that code is referred to as a plugin. 

## Project Structure
This project has over 300 classes and so it will be very difficult to navigate this repo without explaining the structure. 
```
.
├── LICENSE
├── README.md
├── abandoned: Modules that I started but either did not complete or stopped maintaining
├── core: The core library that all the modules use
└── modules: All of the completed / maintained modules that I wrote using this library. 
```
### Core Module 
Below is the tree structure for the Core module. I annoted some of the classes to explain what they do
```
.
├── UltraLib.java ---------------------- This is the top-level module. Control is passed to me here.
├── UltraListener.java ----------------- A set of listeners for certain actions from players
├── UltraObject.java ------------------- Base level object containing core library functionality. \
                            Objects that extend from here support a range of functionality from auto-saving to clean editing in game.
├── commands --------------------------- One of the useful features of my library is that it contains a command parser \  
                                    users in the game can execute commands to interact with my plugin. \    
                                    library handles permissions of who can execute what commands to interface with data
│   ├── BaseUltraCommand.java
│   ├── Component.java
│   ├── UltraBukkitCommand.java
│   └── UltraCommand.java
├── editor ----------------------------- Weather it be designing "spells" in game or creating "quests," \
                                the library supports allowing users in-game to interface directly, \
                                including creating, deleteting, and modifying any objects that extend from UltraObject
│   ├── DisplayValue.java
│   ├── Editor.java
│   ├── EditorCheck.java
│   ├── EditorCommand.java
│   ├── EditorData.java
│   ├── EditorOld.java
│   ├── EditorSettings.java
│   ├── FieldDescription.java
│   ├── SettingsCommand.java
│   └── editors
│       ├── AbstractEditor.java
│       ├── CollectionEditor.java
│       ├── MapEditor.java
│       ├── NewObjectEditor.java
│       └── ObjectEditor.java
├── events ------------------------------ I decided it would be useful to have my own event manager \
                                                to interface cleaner with my other classes
│   ├── EditorRunResponseEvent.java
│   ├── EntityDeathByEntityEvent.java
│   ├── PlayerDataCreationEvent.java
│   ├── ServerTickEvent.java
│   ├── UltraObjectCreationEvent.java
│   └── utility
│       ├── CancelableEvent.java
│       └── UltraEvent.java
├── modules ----------------------------- This handles the module loading. It will dynamically load \
                                    the other modules from their jar files. It will also automatically \
                                    register their listeners, data types, commands, etc, etc
│   ├── Module.java
│   ├── ModuleCommand.java
│   └── ModulesCommand.java
├── network ----------------------------- For a while I had a network of multiple servers and wanted \
                                    a clean way to interface between them, including things such as \
                                    sending chat messages from one to the other, and even sending \
                                    complicated objects such as Spells or Quests (UltraObject derivitves) 
│   ├── FilePacket.java
│   ├── NetworkCommand.java
│   ├── NetworkConnection.java
│   ├── NetworkListener.java
│   ├── Packet.java
│   ├── PacketCommand.java
│   ├── PacketData.java
│   ├── events
│   │   ├── NetworkConnectedEvent.java
│   │   ├── NetworkConnectionEvent.java
│   │   ├── NetworkPacketReceivedEvent.java
│   │   └── PacketCommandEvent.java
│   └── exceptions
│       └── PacketTimedOutException.java
├── player --------------------------------- Stores the core player data. Any of my other modules can register \
                                        data that they want UltraCore to keep track of by simply creating a datatype \
                                        that extends from "PlayerData." UltraCore will handle saving and loading that data \
                                        including with complicated data-types involving graphs and refernces to classes
│   ├── PlayerData.java
│   ├── PlayerReference.java
│   ├── PlayerUtility.java
│   └── UltraPlayer.java
├── tic
│   ├── GameTic.java
│   └── ServerTic.java
└── utils ----------------------------------- This folder contains a bunch of misc functions and features that I \
                                            found I was having to use often in multiple modules
    ├── BinaryHexConverter.java
    ├── NMSUtils.java
    ├── ObjectSerializer.java
    ├── ObjectUtils.java
    ├── savevar
    │   ├── SaveVar.java
    │   └── SaveVariables.java
    └── serialize
        ├── AbstractSerializer.java
        ├── ArraySeralizer.java
        ├── CollectionsSeralizer.java
        ├── MapSerializer.java
        ├── MinecraftItemSerializer.java
        ├── NormalObjectSerializer.java
        ├── ParsableSerializer.java
        └── UltraObjectSerializer.java
```
