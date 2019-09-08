package net.darkhax.indirectcommunicationrelay.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.darkhax.indirectcommunicationrelay.relay.MessageRelay;
import net.darkhax.namespaced.Identifier;

public class MessageRelayTest {
    
    private boolean didReceiveInstantMessage = false;
    
    @Test
    @DisplayName("Test Instant Relay")
    void testInstantMessageRelay () {
        
        // Environment
        final MessageRelay relay = new MessageRelay();
        
        // Recipient
        final Identifier receiverId = new Identifier("test_a", "receive_string");
        
        relay.registerReceiver(receiverId, message -> {
            
            final String receivedString = (String) message.getContents().get();
            Assertions.assertEquals(receivedString, "Hello Test A");
            this.didReceiveInstantMessage = true;
        });
        
        // Sender
        final Identifier senderId = new Identifier("test_b", "send_string");
        final boolean didSend = relay.sendMessage(senderId, receiverId, () -> "Hello Test A");
        Assertions.assertTrue(didSend);
        Assertions.assertTrue(this.didReceiveInstantMessage);
    }
    
    private boolean didReceiveDelayedMessage = false;
    
    @Test
    @DisplayName("Test Delayed Relay")
    void testDelayedMessageRelay () {
        
        // Environment
        final MessageRelay relay = new MessageRelay();
        
        // Recipient
        final Identifier receiverId = new Identifier("test_a", "receive_string");
        relay.registerReceiver(receiverId, message -> {
            
            final String receivedString = (String) message.getContents().get();
            Assertions.assertEquals(receivedString, "Hello Test A");
            this.didReceiveDelayedMessage = true;
        });
        
        // Sender
        final Identifier senderId = new Identifier("test_b", "send_string");
        relay.sendDelayedMessage(senderId, receiverId, () -> "Hello Test A");
        
        // Recipient at a later point
        final boolean didDeliverStuff = relay.requestDelivery(receiverId);
        Assertions.assertTrue(didDeliverStuff);
        Assertions.assertTrue(this.didReceiveDelayedMessage);
    }
}