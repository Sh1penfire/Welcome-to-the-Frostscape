package main.world.blocks;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Eachable;
import main.world.BaseBlock;
import main.world.BaseBuilding;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

public class PlugBlock extends BaseBlock {
    public Seq validFloors;
    protected static float returnCount = 0;
    public DrawBlock drawer;

    public Effect workEffect = Fx.none;
    public float workEffectRange;
    public float effectChance = 0.13f;

    public PlugBlock(String name) {
        super(name);
        validFloors = Seq.with();
        this.drawer = new DrawDefault();
        workEffectRange = 3;
    }


    public TextureRegion[] icons() {
        return this.drawer.finalIcons(this);
    }

    public void load() {
        super.load();
        this.drawer.load(this);
    }

    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        this.drawer.drawPlan(this, plan, list);
    }
    @Override
    public void setBars() {
        super.setBars();
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (isMultiblock()) {
            countSockets(tile);
            if(returnCount > 0) return true;

            //No Tiles of interest found, return here
            return false;
        } else {
            return validFloors.contains(tile.floor());
        }
    }

    protected void countSockets(Tile tile) {
        returnCount = 0;

        for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
            if (validFloors.contains(other.floor())) {
                returnCount++;
            }
        }
    }
    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {

        drawPotentialLinks(x, y);
        drawOverlay((float)(x * 8) + offset, (float)(y * 8) + offset, rotation);

        Tile tile = Vars.world.tile(x, y);
        if (tile != null) {
            countSockets(tile);
            if(returnCount > 0) drawPlaceText(Core.bundle.format("bar.socketsfound", returnCount), x, y, valid);
            else drawPlaceText(Core.bundle.get("bar.nosockets"), x, y, valid);
        }
    }

    public class PlugBuild extends BaseBuilding{
        public float sum;
        public float warmup;

        @Override
        public void updateTile() {
            super.updateTile();
            if(Mathf.chance(warmup * effectChance * delta())) workEffect.at(this.x + Mathf.range(workEffectRange), this.y + Mathf.range(workEffectRange));
        }

        @Override
        public float warmup(){
            return warmup;
        }

        public void draw(){
            drawer.draw(this);
        }


        public void drawLight() {
            super.drawLight();
            drawer.drawLight(this);
        }
        public void onProximityAdded() {
            super.onProximityAdded();
            countSockets(tile);
            this.sum = returnCount;
        }
    }
}
