# Indirect Communication Relay
This library provides a framework for indirect communication between multiple modules in the same runtime environment. The framework allows for indirect interactions which don't require hard dependencies or compile time dependencies. This approach is especially useful when there are modules from many different authors and organizations in the same environment.

## How to use?
To implement this framework simply create a new MessageRelay somewhere in your code base that modules can access. Modules and your application can then register receivers or send messages with the relay during the normal life cycle of your application. Messages can be sent instantly or be delayed until the owner of the receiver is ready to process them.

## Downloads
Coming soon to Maven Central.

## Exampel Code

**Note:** In the following examples new Identifier objects are created for each method call. This is not required and is generally a bad way to write your software. It has been written this way to demonstrate that you don't need the exact same identifier object in order to communicate. It is also done to help demonstrate that the modules can run their code in their own scopes as long as they have access to the relay.

### Instant Messages
```java
        // Base Application Environment
        final MessageRelay relay = new MessageRelay();
        
        // Module A
        relay.registerReceiver(new Identifier("module_a", "receive_string"), message -> {
            
            final String receivedString = (String) message.getContents().get();
            System.out.println("I received a message from " + message.getSender() + " it said " + receivedString);
        });
        
        // Module B
        relay.sendMessage(new Identifier("module_b", "send_string"), new Identifier("module_a", "receive_string"), () -> "Hello module A.");
```

### Delayed Messages
```java
        // Base Application Environment
        final MessageRelay relay = new MessageRelay();
        
        // Module A
        relay.registerReceiver(new Identifier("module_a", "receive_string"), message -> {
            
            final String receivedString = (String) message.getContents().get();
            System.out.println("I received a message from " + message.getSender() + " it said " + receivedString);
        });
        
        // Module B
        relay.sendDelayedMessage(new Identifier("module_b", "send_string"), new Identifier("module_a", "receive_string"), () -> "Hello module A.");
        
        // Module A at some later point
        relay.requestDelivery(new Identifier("module_a", "receive_string"));
```

### Return Messages
```java
        // Base Application Environment
        final MessageRelay relay = new MessageRelay();
        
        // Module A
        relay.registerReceiver(new Identifier("module_a", "receive_string"), message -> {
            
            final String receivedString = (String) message.getContents().get();
            System.out.println("I received a message from " + message.getSender() + " it said " + receivedString);
            
            if (message.getReturnAddress() != null) {
                
                relay.sendMessage(new Identifier("module_a", "receive_string"), message.getReturnAddress(), () -> "Thanks for the message.");
            }
        });
        
        // Module B
        relay.registerReceiver(new Identifier("module_b", "receive_OK"), message -> {
            
            System.out.println("I received an OK from " + message.getSender());
        });
        
        relay.sendMessage(new Identifier("module_b", "send_string"), new Identifier("module_a", "receive_string"), () -> "Hello module A.", new Identifier("module_b", "receive_OK"));
```