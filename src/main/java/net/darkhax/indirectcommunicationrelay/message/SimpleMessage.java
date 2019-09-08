/**
 * This class was created by <Darkhax>. It is distributed as part of the Indirect Communication
 * Relay library under the LGPL 2.1. You can find the original source on GitHub.
 * https://github.com/Darkhax/Indirect-Communication-Relay
 */
package net.darkhax.indirectcommunicationrelay.message;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.darkhax.namespaced.Identifier;

/**
 * A simple default implementation of IMessage. It has no special logic or behaviors.
 * 
 * @author Tyler Hancock (Darkhax)
 */
public class SimpleMessage implements IMessage {
    
    /**
     * The identifier of the sender.
     */
    private final Identifier sender;
    
    /**
     * The contents of the message.
     */
    private final Supplier<?> contents;
    
    /**
     * A potentially null return address for responses to this message.
     */
    @Nullable
    private final Identifier returnAddress;
    
    /**
     * Base constructor for SimpleMessage.
     * 
     * @param sender The identifier of what sent the message.
     * @param contents The contents of the message.
     * @param returnAddress A potentially null return address.
     */
    public SimpleMessage(Identifier sender, Supplier<?> contents, @Nullable Identifier returnAddress) {
        
        this.sender = sender;
        this.contents = contents;
        this.returnAddress = returnAddress;
    }
    
    @Override
    public Identifier getSender () {
        
        return this.sender;
    }
    
    @Override
    public Supplier<?> getContents () {
        
        return this.contents;
    }
    
    @Nullable
    @Override
    public Identifier getReturnAddress () {
        
        return this.returnAddress;
    }
}