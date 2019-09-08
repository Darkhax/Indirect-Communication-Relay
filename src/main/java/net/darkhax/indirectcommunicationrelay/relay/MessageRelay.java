/**
 * This class was created by <Darkhax>. It is distributed as part of the Indirect Communication
 * Relay library under the LGPL 2.1. You can find the original source on GitHub.
 * https://github.com/Darkhax/Indirect-Communication-Relay
 */
package net.darkhax.indirectcommunicationrelay.relay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.darkhax.indirectcommunicationrelay.message.IMessage;
import net.darkhax.indirectcommunicationrelay.message.SimpleMessage;
import net.darkhax.namespaced.Identifier;

/**
 * This class defines a basic object that can relay indirect messages. Messages sent using
 * {@link #sendMessage(Identifier, IMessage)} or
 * {@link #sendMessage(Identifier, Identifier, Supplier, Identifier)} will be delivered
 * instantly.
 * 
 * @author Tyler Hancock (Darkhax)
 */
public class MessageRelay {
    
    /**
     * A backing map used to map message receivers to a namespaced identifier.
     */
    private final Map<Identifier, Consumer<IMessage>> receivers;
    
    /**
     * A map that is used to temporarily hold messages.
     */
    private final Map<Identifier, Collection<IMessage>> mailbox;
    
    /**
     * Default constructor for the message relay. This initializes both the {@link #receivers}
     * and {@link #mailbox} maps as standard Java HashMap objects.
     */
    public MessageRelay() {
        
        this(new HashMap<>(), new HashMap<>());
    }
    
    /**
     * This constructor allows for specific map types to be used when initializing the
     * {@link #receivers} and {@link #mailbox} maps.
     * 
     * @param receiversHolder The map used to hold all message receivers.
     * @param mailbox The map used to hold messages which are not immediately delivered.
     */
    public MessageRelay(Map<Identifier, Consumer<IMessage>> receiversHolder, Map<Identifier, Collection<IMessage>> mailbox) {
        
        this.receivers = receiversHolder;
        this.mailbox = mailbox;
    }
    
    /**
     * Registers an IMessageReceiver with the relay, allowing messages to be sent to it.
     * 
     * @param receiverId The identifier that should be used to send messages to the receiver.
     * @param receiver The receiver that will handle messages.
     * @return The existing receiver mapped to the receiverId. If no previous receiver exists
     *         this will be null.
     */
    @Nullable
    public Consumer<IMessage> registerReceiver (Identifier receiverId, Consumer<IMessage> receiver) {
        
        return this.receivers.put(receiverId, receiver);
    }
    
    /**
     * Attempts to instantly sends a message to a message receiver.
     * 
     * @param senderId The ID of who or what is sending the message.
     * @param recipientId The ID of the recipient. This must be the same as the ID they used
     *        when calling {@link #registerReceiver(Identifier, Consumer)}.
     * @param contents The contents that are being sent.
     * @return Whether or not the message could be delivered.
     */
    public boolean sendMessage (Identifier senderId, Identifier recipientId, Supplier<?> contents) {
        
        return this.sendMessage(recipientId, new SimpleMessage(senderId, contents, null));
    }
    
    /**
     * Attempts to instantly sends a message to a message receiver.
     * 
     * @param senderId The ID of who or what is sending the message.
     * @param recipientId The ID of the recipient. This must be the same as the ID they used
     *        when calling {@link #registerReceiver(Identifier, Consumer)}.
     * @param contents The contents that are being sent.
     * @param returnAddress A potentially null return address that can be used by the receiver
     *        to send responses back.
     * @return Whether or not the message could be delivered.
     */
    public boolean sendMessage (Identifier senderId, Identifier recipientId, Supplier<?> contents, @Nullable Identifier returnAddress) {
        
        return this.sendMessage(recipientId, new SimpleMessage(senderId, contents, returnAddress));
    }
    
    /**
     * Attempts to instantly send a message to a message receiver.
     * 
     * @param recipientId The ID of the recipient. This must be the same as the ID they used
     *        when calling {@link #registerReceiver(Identifier, Consumer)}.
     * @param message The message to send.
     * @return Whether or not the message could be delivered.
     */
    public boolean sendMessage (Identifier recipientId, IMessage message) {
        
        final Consumer<IMessage> receiver = this.receivers.get(recipientId);
        
        if (receiver != null) {
            
            receiver.accept(message);
            return true;
        }
        
        return false;
    }
    
    /**
     * Stores the message in {@link #mailbox} until delivery has been requested using
     * {@link #registerReceiver(Identifier, Consumer)}. Delayed messages allow indirect
     * communication in systems where execution order is unreliable. The existence of a valid
     * recipient is not verified as it may be registered at a later point.
     * 
     * @param senderId The ID of who or what is sending the message.
     * @param recipientId The recipient of the message. The existence of the receiver is not
     *        verified as it may be registered at a later point.
     * @param contents The contents that are being sent.
     */
    public void sendDelayedMessage (Identifier senderId, Identifier recipientId, Supplier<?> contents) {
        
        this.sendDelayedMessage(recipientId, new SimpleMessage(senderId, contents, null));
    }
    
    /**
     * Stores the message in {@link #mailbox} until delivery has been requested using
     * {@link #registerReceiver(Identifier, Consumer)}. Delayed messages allow indirect
     * communication in systems where execution order is unreliable. The existence of a valid
     * recipient is not verified as it may be registered at a later point.
     * 
     * @param senderId The ID of who or what is sending the message.
     * @param recipientId The recipient of the message. The existence of the receiver is not
     *        verified as it may be registered at a later point.
     * @param contents The contents that are being sent.
     * @param returnAddress A potentially null return address that can be used by the receiver
     *        to send responses back.
     */
    public void sendDelayedMessage (Identifier senderId, Identifier recipientId, Supplier<?> contents, @Nullable Identifier returnAddress) {
        
        this.sendDelayedMessage(recipientId, new SimpleMessage(senderId, contents, returnAddress));
    }
    
    /**
     * Stores the message in {@link #mailbox} until delivery has been requested using
     * {@link #registerReceiver(Identifier, Consumer)}. Delayed messages allow indirect
     * communication in systems where execution order is unreliable. The existence of a valid
     * recipient is not verified as it may be registered at a later point.
     * 
     * @param recipientId The recipient of the message. The existence of the receiver is not
     *        verified as it may be registered at a later point.
     * @param message The message to send.
     */
    public void sendDelayedMessage (Identifier recipientId, IMessage message) {
        
        this.mailbox.computeIfAbsent(recipientId, key -> new ArrayList<>()).add(message);
    }
    
    /**
     * Requests that all delayed messages for a specific receiver are delivered.
     * 
     * @param receiverId The ID of the receiver to request.
     * @return Whether or not messages were delivered. This will be false if no receiver could
     *         be found or if there were no messages found for that receiver.
     */
    public boolean requestDelivery (Identifier receiverId) {
        
        final Consumer<IMessage> receiver = this.receivers.get(receiverId);
        final Collection<IMessage> messages = this.mailbox.remove(receiverId);
        
        if (receiver != null && messages != null) {
            
            for (final IMessage message : messages) {
                
                receiver.accept(message);
            }
            
            return !messages.isEmpty();
        }
        
        return false;
    }
}