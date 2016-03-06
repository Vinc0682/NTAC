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

public class WrapperPlayServerMapChunkBulk extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.MAP_CHUNK_BULK;

    public WrapperPlayServerMapChunkBulk() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerMapChunkBulk(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve the x coordinates for each chunk segment.
     * @return Chunk x coordindates.
    */
    public int[] getChunksX() {
        return handle.getIntegerArrays().read(0);
    }

    /**
     * Set the x coordinates for each chunk segment.
     * @param value - new value.
    */
    public void setChunksX(int[] value) {
        handle.getIntegerArrays().write(0, value);
    }

    /**
     * Retrieve the z coordinates for each chunk segment.
     * @return Chunk z coordindates.
    */
    public int[] getChunksZ() {
        return handle.getIntegerArrays().read(1);
    }

    /**
     * Set the z coordinates for each chunk segment.
     * @param value - new value.
    */
    public void setChunksZ(int[] value) {
        handle.getIntegerArrays().write(1, value);
    }

    public Object[] getChunks() {
        return (Object[]) handle.getModifier().read(2);
    }

    public void setChunks(Object[] value) {
        handle.getModifier().write(2, value);
    }

    /**
     * Retrieve Sky light sent.
     * <p>
     * Notes: whether or not the chunk data contains a light nibble array. This is true in the main world, false in the end + nether
     * @return The current Sky light sent
     */
    public boolean isSkyLightSent() {
        return handle.getBooleans().read(0);
    }

    /**
     * Set Sky light sent.
     * @param value - new value.
     */
    public void setSkyLightSent(boolean value) {
        handle.getBooleans().write(0, value);
    }
}
