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


```
.
├── UltraLib.java
├── UltraListener.java
├── UltraObject.java
├── commands
│   ├── BaseUltraCommand.java
│   ├── Component.java
│   ├── UltraBukkitCommand.java
│   └── UltraCommand.java
├── editor
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
├── events
│   ├── EditorRunResponseEvent.java
│   ├── EntityDeathByEntityEvent.java
│   ├── PlayerDataCreationEvent.java
│   ├── ServerTickEvent.java
│   ├── UltraObjectCreationEvent.java
│   └── utility
│       ├── CancelableEvent.java
│       └── UltraEvent.java
├── modules
│   ├── Module.java
│   ├── ModuleCommand.java
│   └── ModulesCommand.java
├── network
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
├── player
│   ├── PlayerData.java
│   ├── PlayerReference.java
│   ├── PlayerUtility.java
│   └── UltraPlayer.java
├── tic
│   ├── GameTic.java
│   └── ServerTic.java
└── utils
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
