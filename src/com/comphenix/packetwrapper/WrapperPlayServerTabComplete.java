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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerTabComplete extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.TAB_COMPLETE;
    
    public WrapperPlayServerTabComplete() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerTabComplete(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Count.
     * <p>
     * Notes: number of following strings
     * @return The current Count
     */
    public int getCount() {
        return handle.getStringArrays().read(0).length;
    }
    
    /**
     * Retrieve Match.
     * <p>
     * Notes: one eligible command, note that each command is sent separately instead of in a single string, hence the need for Count
     * @return The current Match
     */
    public String[] getMatches() {
        return handle.getStringArrays().read(0);
    }
    
    /**
     * Set Match.
     * @param value - new value.
     */
    public void setMatches(String[] value) {
        handle.getStringArrays().write(0, value);
    }
    
}

