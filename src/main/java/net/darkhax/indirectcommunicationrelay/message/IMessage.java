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
 * This interface defines the required structure of an indirect message.
 * 
 * @author Tyler Hancock (Darkhax)
 */
public interface IMessage {
    
    /**
     * Gets the identifier of what sent the message.
     * 
     * @return The identifier of what sent the message.
     */
    Identifier getSender ();
    
    /**
     * Gets the contents of the message as a wildcard supplier.
     * 
     * @return The contents of the message as a wildcard supplier.
     */
    Supplier<?> getContents ();
    
    /**
     * Gets a return address for this message. This is an optional value that can be used to
     * sent indirect messages back to the sender.
     * 
     * @return An identifier that can be used to send responses back to the sender. It may be
     *         null.
     */
    @Nullable
    Identifier getReturnAddress ();
}