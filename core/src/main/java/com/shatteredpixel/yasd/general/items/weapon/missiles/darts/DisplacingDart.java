/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Yet Another Shattered Dungeon
 *  * Copyright (C) 2014-2020 Samuel Braithwaite
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *
 */

package com.shatteredpixel.yasd.general.items.weapon.missiles.darts;

import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.actors.Actor;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.yasd.general.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;

public class DisplacingDart extends TippedDart {
	
	{
		image = ItemSpriteSheet.DISPLACING_DART;
	}
	
	int distance = 8;
	
	@Override
	public int proc(Char attacker, Char defender, int damage) {
		
		if (!defender.properties().contains(Char.Property.IMMOVABLE)){
			
			int startDist = Dungeon.level.distance(attacker.pos, defender.pos);
			
			HashMap<Integer, ArrayList<Integer>> positions = new HashMap<>();
			
			for (int pos = 0; pos < Dungeon.level.length(); pos++){
				if (Dungeon.level.heroFOV[pos]
						&& Dungeon.level.passable(pos)
						&& defender.canOccupy(Dungeon.level, pos)
						&& Actor.findChar(pos) == null){
					
					int dist = Dungeon.level.distance(attacker.pos, pos);
					if (dist > startDist){
						if (positions.get(dist) == null){
							positions.put(dist, new ArrayList<Integer>());
						}
						positions.get(dist).add(pos);
					}
					
				}
			}
			
			float[] probs = new float[distance+1];
			
			for (int i = 0; i <= distance; i++){
				if (positions.get(i) != null){
					probs[i] = i - startDist;
				}
			}
			
			int chosenDist = Random.chances(probs);
			
			if (chosenDist != -1){
				int pos = positions.get(chosenDist).get(Random.index(positions.get(chosenDist)));
				ScrollOfTeleportation.appear( defender, pos );
				Dungeon.level.occupyCell(defender );
			}
		
		}
		
		return super.proc(attacker, defender, damage);
	}
}
