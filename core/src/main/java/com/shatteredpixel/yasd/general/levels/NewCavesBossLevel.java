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

package com.shatteredpixel.yasd.general.levels;

import com.shatteredpixel.yasd.general.Assets;
import com.shatteredpixel.yasd.general.Bones;
import com.shatteredpixel.yasd.general.Dungeon;
import com.shatteredpixel.yasd.general.Element;
import com.shatteredpixel.yasd.general.actors.Actor;
import com.shatteredpixel.yasd.general.actors.Char;
import com.shatteredpixel.yasd.general.actors.blobs.Blob;
import com.shatteredpixel.yasd.general.actors.blobs.Electricity;
import com.shatteredpixel.yasd.general.actors.mobs.OldDM300;
import com.shatteredpixel.yasd.general.actors.mobs.Mob;
import com.shatteredpixel.yasd.general.actors.mobs.NewDM300;
import com.shatteredpixel.yasd.general.actors.mobs.Pylon;
import com.shatteredpixel.yasd.general.effects.BlobEmitter;
import com.shatteredpixel.yasd.general.effects.CellEmitter;
import com.shatteredpixel.yasd.general.effects.Speck;
import com.shatteredpixel.yasd.general.effects.particles.BlastParticle;
import com.shatteredpixel.yasd.general.effects.particles.SparkParticle;
import com.shatteredpixel.yasd.general.items.Heap;
import com.shatteredpixel.yasd.general.items.Item;
import com.shatteredpixel.yasd.general.levels.interactive.DescendArea;
import com.shatteredpixel.yasd.general.levels.painters.CavesPainter;
import com.shatteredpixel.yasd.general.levels.painters.Painter;
import com.shatteredpixel.yasd.general.levels.terrain.Terrain;
import com.shatteredpixel.yasd.general.messages.Messages;
import com.shatteredpixel.yasd.general.scenes.GameScene;
import com.shatteredpixel.yasd.general.sprites.CharSprite;
import com.shatteredpixel.yasd.general.tiles.CustomTilemap;
import com.shatteredpixel.yasd.general.tiles.DungeonTilemap;
import com.shatteredpixel.yasd.general.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.ArrayList;

import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.CHASM;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.EMPTY;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.EMPTY_SP;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.ENTRANCE;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.EXIT;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.INACTIVE_TRAP;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.NONE;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.SIGN;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.STATUE;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.WALL;
import static com.shatteredpixel.yasd.general.levels.terrain.Terrain.WATER;

public class NewCavesBossLevel extends Level {

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	public String tilesTex() {
		return Assets.TILES_CAVES;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_CAVES;
	}

	@Override
	public String loadImg() {
		return Assets.LOADING_CAVES;
	}

	private static int WIDTH = 33;
	private static int HEIGHT = 42;

	public static Rect mainArena = new Rect(5, 14, 28, 37);
	public static Rect gate = new Rect(14, 13, 19, 14);
	public static int[] pylonPositions = new int[]{ 4 + 13*WIDTH, 28 + 13*WIDTH, 4 + 37*WIDTH, 28 + 37*WIDTH };

	private ArenaVisuals customArenaVisuals;

	@Override
	protected boolean build() {

		setSize(WIDTH, HEIGHT);

		//Painter.fill(this, 0, 0, width(), height(), Terrain.EMBERS);

		//setup exit area above main boss arena
		Painter.fill(this, 0, 3, width(), 4, CHASM);
		Painter.fill(this, 6, 7, 21, 1, CHASM);
		Painter.fill(this, 10, 8, 13, 1, CHASM);
		Painter.fill(this, 12, 9, 9, 1, CHASM);
		Painter.fill(this, 13, 10, 7, 1, CHASM);
		Painter.fill(this, 14, 3, 5, 10, EMPTY);

		//fill in special floor, statues, and exits
		Painter.fill(this, 15, 2, 3, 3, EMPTY_SP);
		Painter.fill(this, 15, 5, 3, 1, STATUE);
		Painter.fill(this, 15, 7, 3, 1, STATUE);
		Painter.fill(this, 15, 9, 3, 1, STATUE);
		Painter.fill(this, 16, 5, 1, 6, EMPTY_SP);
		//Set the large exit.
		interactiveAreas.add(new DescendArea().setPos(15, 0, 3, 3));
		Painter.fill(this, 15, 0, 3, 3, EXIT);

		//setExit(16 + 2*width());

		//These signs are visually overridden with custom tile visuals
		Painter.fill(this, gate, SIGN);

		//set up main boss arena
		Painter.fillEllipse(this, mainArena, EMPTY);

		boolean[] patch = Patch.generate( width, height-14, 0.20f, 2, true );
		for (int i= 14*width(); i < length(); i++) {
			if (map[i] == EMPTY) {
				if (patch[i - 14*width()]){
					map[i] = WATER;
				} else if (Random.Int(6) == 0){
					map[i] = INACTIVE_TRAP;
					map[i] = INACTIVE_TRAP;
				}
			}
		}

		buildEntrance();
		buildCorners();

		CustomTilemap customVisuals = new CityEntrance();
		customVisuals.setRect(0, 0, width(), 11);
		customTiles.add(customVisuals);

		customVisuals = new EntranceOverhang();
		customVisuals.setRect(0, 0, width(), 11);
		customWalls.add(customVisuals);

		customVisuals = customArenaVisuals = new ArenaVisuals();
		customVisuals.setRect(0, 12, width(), 27);
		customTiles.add(customVisuals);

		new CavesPainter().paint(this, null);

		return true;

	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);

		for (CustomTilemap c : customTiles){
			if (c instanceof ArenaVisuals){
				customArenaVisuals = (ArenaVisuals) c;
			}
		}
	}

	@Override
	protected void createMobs() { }

	@Override
	public Actor respawner() {
		return null;
	}

	@Override
	protected void createItems() {
		Item item = Bones.get();
		if (item != null) {
			int pos;
			do {
				pos = randomRespawnCell(null);
			} while (pos == getEntrancePos());
			drop( item, pos ).setHauntedIfCursed().type = Heap.Type.REMAINS;
		}
	}

	@Override
	public int randomRespawnCell( Char ch ) {
		int cell;
		do {
			cell = getEntrancePos() + PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (!passable(cell)
				|| !Char.canOccupy(ch, this, cell)
				|| Actor.findChar(cell) != null);
		return cell;
	}

	@Override
	public void occupyCell(Char ch) {
		super.occupyCell(ch);

		//seal the level when the hero moves off the entrance, the level isn't already sealed, and the gate hasn't been destroyed
		int gatePos = pointToCell(new Point(gate.left, gate.top));
		if (ch == Dungeon.hero && ch.pos != getEntrancePos() && !locked && solid(gatePos)){

			seal();

		}
	}

	@Override
	public void seal() {
		super.seal();

		NewDM300 boss = Mob.create(NewDM300.class, this);
		boss.state = boss.WANDERING;
		do {
			boss.pos = pointToCell(Random.element(mainArena.getPoints()));
		} while (!openSpace(boss.pos) || map[boss.pos] == EMPTY_SP || heroFOV[boss.pos]);
		GameScene.add( boss );

		set( getEntrancePos(), WALL );
		GameScene.updateMap( getEntrancePos() );
		Dungeon.observe();

		CellEmitter.get( getEntrancePos() ).start( Speck.factory( Speck.ROCK ), 0.07f, 10 );
		Camera.main.shake( 3, 0.7f );
		Sample.INSTANCE.play( Assets.SND_ROCKS );

		for (int i : pylonPositions) {
			Pylon pylon = new Pylon();
			pylon.pos = i;
			GameScene.add(pylon);
		}

	}

	@Override
	public void unseal() {
		super.unseal();

		blobs.get(PylonEnergy.class).fullyClear();

		set( getEntrancePos(), ENTRANCE );
		int i = 14 + 13*width();
		for (int j = 0; j < 5; j++){
			set( i+j, EMPTY );
			if (Dungeon.level.heroFOV[i+j]){
				CellEmitter.get(i+j).burst(BlastParticle.FACTORY, 10);
			}
		}
		GameScene.updateMap();

		customArenaVisuals.updateState();

		Dungeon.observe();

	}

	public void activatePylon(){
		ArrayList<Pylon> pylons = new ArrayList<>();
		for (Mob m : mobs){
			if (m instanceof Pylon && m.alignment == Char.Alignment.NEUTRAL){
				pylons.add((Pylon) m);
			}
		}

		if (pylons.size() == 1){
			pylons.get(0).activate();
		} else if (!pylons.isEmpty()) {
			Pylon closest = null;
			for (Pylon p : pylons){
				if (closest == null || trueDistance(p.pos, Dungeon.hero.pos) < trueDistance(closest.pos, Dungeon.hero.pos)){
					closest = p;
				}
			}
			pylons.remove(closest);
			Random.element(pylons).activate();
		}

		for( int i = (mainArena.top-1)*width; i <length; i++){
			if (map[i] == INACTIVE_TRAP || map[i] == WATER || map[i] == SIGN){
				GameScene.add(Blob.seed(i, 1, PylonEnergy.class));
			}
		}

	}

	public void eliminatePylon(){
		customArenaVisuals.updateState();
		int pylonsRemaining = 0;
		for (Mob m : mobs){
			if (m instanceof NewDM300){
				((NewDM300) m).loseSupercharge();
				PylonEnergy.energySourceSprite = m.sprite;
			} else if (m instanceof Pylon){
				pylonsRemaining++;
			}
		}
		if (pylonsRemaining > 2) {
			blobs.get(PylonEnergy.class).fullyClear();
		}
	}

	@Override
	public String tileName( Terrain tile ) {
		switch (tile) {
			case GRASS:
				return Messages.get(CavesLevel.class, "grass_name");
			case HIGH_GRASS:
				return Messages.get(CavesLevel.class, "high_grass_name");
			case WATER:
				return Messages.get(CavesLevel.class, "water_name");
			case STATUE:
				//city statues are used
				return Messages.get(CityLevel.class, "statue_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc( Terrain tile ) {
		switch (tile) {
			case WATER:
				return super.tileDesc( tile ) + "\n\n" + Messages.get(NewCavesBossLevel.class, "water_desc");
			case ENTRANCE:
				return Messages.get(CavesLevel.class, "entrance_desc");
			case EXIT:
				//city exit is used
				return Messages.get(CityLevel.class, "exit_desc");
			case HIGH_GRASS:
				return Messages.get(CavesLevel.class, "high_grass_desc");
			case WALL_DECO:
				return Messages.get(CavesLevel.class, "wall_deco_desc");
			case BOOKSHELF:
				return Messages.get(CavesLevel.class, "bookshelf_desc");
			//city statues are used
			case STATUE:
				return Messages.get(CityLevel.class, "statue_desc");
			default:
				return super.tileDesc( tile );
		}
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		CavesLevel.addCavesVisuals(this, visuals);
		return visuals;
	}

	/**
	 * semi-randomized setup for entrance and corners
	 */

	private static final Terrain n = NONE; //used when a tile shouldn't be changed
	private static final Terrain W = WALL;
	private static final Terrain e = EMPTY;
	private static final Terrain s = EMPTY_SP;

	private static Terrain[] entrance1 = {
			n, n, n, n, n, n, n, n,
			n, n, n, n, n, n, n, n,
			n, n, n, n, W, e, W, W,
			n, n, n, W, W, e, W, W,
			n, n, W, W, e, e, e, e,
			n, n, e, e, e, W, W, e,
			n, n, W, W, e, W, e, e,
			n, n, W, W, e, e, e, e
	};

	private static Terrain[] entrance2 = {
			n, n, n, n, n, n, n, n,
			n, n, n, n, n, n, W, W,
			n, n, n, n, n, n, e, e,
			n, n, n, n, e, W, W, W,
			n, n, n, e, e, e, e, e,
			n, n, n, W, e, W, W, e,
			n, W, e, W, e, W, e, e,
			n, W, e, W, e, e, e, e
	};

	private static Terrain[] entrance3 = {
			n, n, n, n, n, n, n, n,
			n, n, n, n, n, n, n, n,
			n, n, n, n, n, n, n, n,
			n, n, n, W, W, e, W, W,
			n, n, n, W, W, e, W, W,
			n, n, n, e, e, e, e, e,
			n, n, n, W, W, e, W, e,
			n, n, n, W, W, e, e, e
	};

	private static Terrain[] entrance4 = {
			n, n, n, n, n, n, n, n,
			n, n, n, n, n, n, n, e,
			n, n, n, n, n, n, W, e,
			n, n, n, n, n, W, W, e,
			n, n, n, n, W, W, W, e,
			n, n, n, W, W, W, W, e,
			n, n, W, W, W, W, e, e,
			n, e, e, e, e, e, e, e
	};

	private static Terrain[][] entranceVariants = {
			entrance1,
			entrance2,
			entrance3,
			entrance4
	};

	private void buildEntrance(){
		setEntrance(16 + 25*width());

		//entrance area
		int NW = getEntrancePos() - 7 - 7*width();
		int NE = getEntrancePos() + 7 - 7*width();
		int SE = getEntrancePos() + 7 + 7*width();
		int SW = getEntrancePos() - 7 + 7*width();

		Terrain[] entranceTiles = Random.oneOf(entranceVariants);
		for (int i = 0; i < entranceTiles.length; i++){
			if (i % 8 == 0 && i != 0){
				NW += (width() - 8);
				NE += (width() + 8);
				SE -= (width() - 8);
				SW -= (width() + 8);
			}

			if (entranceTiles[i] != n) map[NW] = map[NE] = map[SE] = map[SW] = entranceTiles[i];
			NW++; NE--; SW++; SE--;
		}

		Painter.set(this, getEntrancePos(), ENTRANCE);
	}

	private static Terrain[] corner1 = {
			W, W, W, W, W, W, W, W, W, W,
			W, s, s, s, e, e, e, W, W, W,
			W, s, s, s, W, W, e, e, W, W,
			W, s, s, s, W, W, W, e, e, W,
			W, e, W, W, W, W, W, W, e, n,
			W, e, W, W, W, W, W, n, n, n,
			W, e, e, W, W, W, n, n, n, n,
			W, W, e, e, W, n, n, n, n, n,
			W, W, W, e, e, n, n, n, n, n,
			W, W, W, W, n, n, n, n, n, n,
	};

	private static Terrain[] corner2 = {
			W, W, W, W, W, W, W, W, W, W,
			W, s, s, s, W, W, W, W, W, W,
			W, s, s, s, e, e, e, e, e, W,
			W, s, s, s, W, W, W, W, e, e,
			W, W, e, W, W, W, W, W, W, e,
			W, W, e, W, W, W, W, n, n, n,
			W, W, e, W, W, W, n, n, n, n,
			W, W, e, W, W, n, n, n, n, n,
			W, W, e, e, W, n, n, n, n, n,
			W, W, W, e, e, n, n, n, n, n,
	};

	private static Terrain[] corner3 = {
			W, W, W, W, W, W, W, W, W, W,
			W, s, s, s, W, e, e, e, W, W,
			W, s, s, s, e, e, W, e, W, W,
			W, s, s, s, W, W, W, e, W, W,
			W, W, e, W, W, W, W, e, W, n,
			W, e, e, W, W, W, W, e, e, n,
			W, e, W, W, W, W, n, n, n, n,
			W, e, e, e, e, e, n, n, n, n,
			W, W, W, W, W, e, n, n, n, n,
			W, W, W, W, n, n, n, n, n, n,
	};

	private static Terrain[] corner4 = {
			W, W, W, W, W, W, W, W, W, W,
			W, s, s, s, W, W, W, W, W, W,
			W, s, s, s, e, e, e, W, W, W,
			W, s, s, s, W, W, e, W, W, W,
			W, W, e, W, W, W, e, W, W, n,
			W, W, e, W, W, W, e, e, n, n,
			W, W, e, e, e, e, e, n, n, n,
			W, W, W, W, W, e, n, n, n, n,
			W, W, W, W, W, n, n, n, n, n,
			W, W, W, W, n, n, n, n, n, n,
	};

	private static Terrain[][] cornerVariants = {
			corner1,
			corner2,
			corner3,
			corner4
	};

	private void buildCorners(){
		int NW = 2 + 11*width();
		int NE = 30 + 11*width();
		int SE = 30 + 39*width();
		int SW = 2 + 39*width();

		Terrain[] cornerTiles = Random.oneOf(cornerVariants);
		for(int i = 0; i < cornerTiles.length; i++){
			if (i % 10 == 0 && i != 0){
				NW += (width() - 10);
				NE += (width() + 10);
				SE -= (width() - 10);
				SW -= (width() + 10);
			}

			if (cornerTiles[i] != n) map[NW] = map[NE] = map[SE] = map[SW] = cornerTiles[i];
			NW++; NE--; SW++; SE--;
		}
	}

	/**
	 * Visual Effects
	 */

	public static class CityEntrance extends CustomTilemap{

		{
			texture = Assets.CAVES_BOSS;
		}

		private static short[] entryWay = new short[]{
				-1,  7,  7,  7, -1,
				-1,  1,  2,  3, -1,
				8,  1,  2,  3, 12,
				16,  9, 10, 11, 20,
				16, 16, 18, 20, 20,
				16, 17, 18, 19, 20,
				16, 16, 18, 20, 20,
				16, 17, 18, 19, 20,
				16, 16, 18, 20, 20,
				16, 17, 18, 19, 20,
				24, 25, 26, 27, 28
		};

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			int[] data = new int[tileW*tileH];
			int entryPos = 0;
			for (int i = 0; i < data.length; i++){

				//override the entryway
				if (i % tileW == tileW/2 - 2){
					data[i++] = entryWay[entryPos++];
					data[i++] = entryWay[entryPos++];
					data[i++] = entryWay[entryPos++];
					data[i++] = entryWay[entryPos++];
					data[i] = entryWay[entryPos++];

					//otherwise check if we are on row 2 or 3, in which case we need to override walls
				} else {
					if (i / tileW == 2) data[i] = 13;
					else if (i / tileW == 3) data[i] = 21;
					else data[i] = -1;
				}
			}
			v.map( data, tileW );
			return v;
		}

	}

	public static class EntranceOverhang extends CustomTilemap{

		{
			texture = Assets.CAVES_BOSS;
		}

		private static short[] entryWay = new short[]{
				0,  7,  7,  7,  4,
				0, 15, 15, 15,  4,
				8, 23, 23, 23, 12,
				-1, -1, -1, -1, -1,
				-1,  6, -1, 14, -1,
				-1, -1, -1, -1, -1,
				-1,  6, -1, 14, -1,
				-1, -1, -1, -1, -1,
				-1,  6, -1, 14, -1,
				-1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1,
		};

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			int[] data = new int[tileW*tileH];
			int entryPos = 0;
			for (int i = 0; i < data.length; i++){

				//copy over this row of the entryway
				if (i % tileW == tileW/2 - 2){
					data[i++] = entryWay[entryPos++];
					data[i++] = entryWay[entryPos++];
					data[i++] = entryWay[entryPos++];
					data[i++] = entryWay[entryPos++];
					data[i] = entryWay[entryPos++];
				} else {
					data[i] = -1;
				}
			}
			v.map( data, tileW );
			return v;
		}

	}

	public static class ArenaVisuals extends CustomTilemap {

		{
			texture = Assets.CAVES_BOSS;
		}

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			updateState( );

			return v;
		}

		public void updateState( ){
			if (vis != null){
				int[] data = new int[tileW*tileH];
				int j = Dungeon.level.width() * tileY;
				for (int i = 0; i < data.length; i++){

					if (Dungeon.level.map[j] == EMPTY_SP) {
						for (int k : pylonPositions) {
							if (k == j) {
								if (Dungeon.level.locked
										&& !(Actor.findChar(k) instanceof Pylon)) {
									data[i] = 38;
								} else {
									data[i] = -1;
								}
							} else if (Dungeon.level.adjacent(k, j)) {
								int w = Dungeon.level.width;
								data[i] = 54 + (j % w + 8 * (j / w)) - (k % w + 8 * (k / w));
							}
						}
					} else if (Dungeon.level.map[j] == INACTIVE_TRAP){
						data[i] = 37;
					} else if (gate.inside(Dungeon.level.cellToPoint(j))){
						int idx = Dungeon.level.solid(j) ? 40 : 32;
						data[i++] = idx++;
						data[i++] = idx++;
						data[i++] = idx++;
						data[i++] = idx++;
						data[i] = idx;
						j += 4;
					} else {
						data[i] = -1;
					}

					j++;
				}
				vis.map(data, tileW);
			}
		}

		@Override
		public String name(int tileX, int tileY) {
			int i = tileX + tileW*(tileY + this.tileY);
			if (Dungeon.level.map[i] == INACTIVE_TRAP){
				return Messages.get(NewCavesBossLevel.class, "wires_name");
			} else if (gate.inside(Dungeon.level.cellToPoint(i))){
				return Messages.get(NewCavesBossLevel.class, "gate_name");
			}

			return super.name(tileX, tileY);
		}

		@Override
		public String desc(int tileX, int tileY) {
			int i = tileX + tileW*(tileY + this.tileY);
			if (Dungeon.level.map[i] == INACTIVE_TRAP){
				return Messages.get(NewCavesBossLevel.class, "wires_desc");
			} else if (gate.inside(Dungeon.level.cellToPoint(i))){
				if (Dungeon.level.solid(i)){
					return Messages.get(NewCavesBossLevel.class, "gate_desc");
				} else {
					return Messages.get(NewCavesBossLevel.class, "gate_desc_broken");
				}
			}
			return super.desc(tileX, tileY);
		}

		@Override
		public Image image(int tileX, int tileY) {
			int i = tileX + tileW*(tileY + this.tileY);
			for (int k : pylonPositions){
				if (Dungeon.level.distance(i, k) <= 1){
					return null;
				}
			}

			return super.image(tileX, tileY);

		}
	}

	public static class PylonEnergy extends Blob {

		@Override
		protected void evolve() {
			int cell;
			for (int i=area.top-1; i <= area.bottom; i++) {
				for (int j = area.left-1; j <= area.right; j++) {
					cell = j + i* Dungeon.level.width();
					if (Dungeon.level.insideMap(cell)) {
						off[cell] = cur[cell];
						volume += off[cell];
						if (off[cell] > 0){

							Char ch = Actor.findChar(cell);
							if (ch != null && !(ch instanceof NewDM300)) {
								Sample.INSTANCE.play( Assets.SND_LIGHTNING );
								ch.damage( Random.NormalIntRange(6, 12), new Char.DamageSrc(Element.ELECTRIC, this));
								ch.sprite.flash();

								if (ch == Dungeon.hero && !ch.isAlive()) {
									Dungeon.fail(NewDM300.class);
									GLog.n( Messages.get(Electricity.class, "ondeath") );
								}
							}
						}
					}
				}
			}
		}

		@Override
		public void fullyClear() {
			super.fullyClear();
			energySourceSprite = null;
		}

		private static CharSprite energySourceSprite = null;

		private static Emitter.Factory DIRECTED_SPARKS = new Emitter.Factory() {
			@Override
			public void emit(Emitter emitter, int index, float x, float y) {
				if (energySourceSprite == null){
					for (Char c : Actor.chars()){
						if (c instanceof Pylon && c.alignment != Char.Alignment.NEUTRAL){
							energySourceSprite = c.sprite;
							break;
						} else if (c instanceof OldDM300){
							energySourceSprite = c.sprite;
						}
					}
					if (energySourceSprite == null){
						return;
					}
				}

				SparkParticle s = ((SparkParticle) emitter.recycle(SparkParticle.class));
				s.resetStatic(x, y);
				s.speed.set((energySourceSprite.x + energySourceSprite.width/2f) - x,
						(energySourceSprite.y + energySourceSprite.height/2f) - y);
				s.speed.normalize().scale(DungeonTilemap.SIZE/2);
				s.acc.set(s.speed);
			}

			@Override
			public boolean lightMode() {
				return true;
			}
		};

		@Override
		public String tileDesc() {
			return Messages.get(NewCavesBossLevel.class, "energy_desc");
		}
		@Override
		public void use( BlobEmitter emitter ) {
			super.use( emitter );
			energySourceSprite = null;
			//emitter.bound.set( 4/16f, 4/16f, 12/16f, 12/16f);
			emitter.pour(DIRECTED_SPARKS, 0.2f);
		}

	}
}