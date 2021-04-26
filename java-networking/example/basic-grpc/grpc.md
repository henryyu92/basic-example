### gRPC

At a high level there are three distinct layers to the library: Stub, Channel, and Transport.

#### Stub
 
The Stub layer is what is exposed to most developers and provides type-safe bindings to whatever datamodel/IDL/interface you are adapting. gRPC comes with a plugin to the protocol-buffers compiler that generates Stub interfaces out of .proto files, but bindings to other datamodel/IDL are easy and encouraged.
 
#### Channel
 
The Channel layer is an abstraction over Transport handling that is suitable for interception/decoration and exposes more behavior to the application than the Stub layer. It is intended to be easy for application frameworks to use this layer to address cross-cutting concerns such as logging, monitoring, auth, etc.
 
#### Transport

The Transport layer does the heavy lifting of putting and taking bytes off the wire. The interfaces to it are abstract just enough to allow plugging in of different implementations. Note the transport layer API is considered internal to gRPC and has weaker API guarantees than the core API under package io.grpc.

gRPC comes with three Transport implementations:

1. The Netty-based transport is the main transport implementation based on Netty. It is for both the client and the server.
2. The OkHttp-based transport is a lightweight transport based on OkHttp. It is mainly for use on Android and is for client only.
3. The in-process transport is for when a server is in the same process as the client. It is useful for testing, while also being safe for production use.