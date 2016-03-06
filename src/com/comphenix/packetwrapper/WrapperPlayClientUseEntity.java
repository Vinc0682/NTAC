/**
 * This file is part of PacketWrapper.
 * Copyright (C) 2012-2015 Kristian S. Strangeland
 * Copyright (C) 2015 dmulloy2
 *
 * PacketWrapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PacketWrapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PacketWrapper.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.comphenix.packetwrapper;

import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

public class WrapperPlayClientUseEntity extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.USE_ENTITY;
    
    public WrapperPlayClientUseEntity() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientUseEntity(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Target.
     * @return The current Target
     */
    public int getTarget() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Target.
     * @param value - new value.
     */
    public void setTarget(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Type.
     * <p>
     * Notes: 0 = INTERACT, 1 = ATTACK, 2 = INTERACT_AT
     * @return The current Type
     */
    public EntityUseAction getType() {
        return handle.getEntityUseActions().read(0);
    }
    
    /**
     * Set Type.
     * @param value - new value.
     */
    public void setType(EntityUseAction value) {
        handle.getEntityUseActions().write(0, value);
    }

    public Vector getTargetVector() {
    	return handle.getVectors().read(0);
    }

    public void setTargetVector(Vector value) {
    	handle.getVectors().write(0, value);
    }
 
}
