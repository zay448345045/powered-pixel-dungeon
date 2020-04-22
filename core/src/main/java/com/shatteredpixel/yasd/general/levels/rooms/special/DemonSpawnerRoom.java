/*
 *
 *   Pixel Dungeon
 *   Copyright (C) 2012-2015 Oleg Dolya
 *
 *   Shattered Pixel Dungeon
 *   Copyright (C) 2014-2019 Evan Debenham
 *
 *   Yet Another Shattered Dungeon
 *   Copyright (C) 2014-2020 Samuel Braithwaite
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *
 */

package com.shatteredpixel.yasd.general.levels.rooms.special;

import com.shatteredpixel.yasd.general.actors.mobs.DemonSpawner;
import com.shatteredpixel.yasd.general.actors.mobs.RipperDemon;
import com.shatteredpixel.yasd.general.levels.Level;
import com.shatteredpixel.yasd.general.levels.painters.Painter;
import com.shatteredpixel.yasd.general.levels.rooms.Room;
import com.shatteredpixel.yasd.general.levels.rooms.standard.EntranceRoom;
import com.shatteredpixel.yasd.general.levels.terrain.Terrain;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class DemonSpawnerRoom extends SpecialRoom {
	@Override
	public void paint(Level level) {

		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY );

		Point c = center();
		int cx = c.x;
		int cy = c.y;

		Door door = entrance();
		door.set(Door.Type.UNLOCKED);

		DemonSpawner spawner = new DemonSpawner();
		spawner.pos = cx + cy * level.width();
		level.mobs.add( spawner );

		int rippers = Random.IntRange(1, 2);

		for (int i = 0; i < rippers; i++){
			int pos;
			do {
				pos = level.pointToCell(random(1));
			} while (level.solid(pos) || level.findMob(pos) != null);

			RipperDemon ripper = new RipperDemon();
			ripper.pos = pos;
			ripper.state = ripper.HUNTING;
			level.mobs.add( ripper );
		}

	}

	@Override
	public boolean connect(Room room) {
		//cannot connect to entrance, otherwise works normally
		if (room instanceof EntranceRoom) return false;
		else                              return super.connect(room);
	}
}