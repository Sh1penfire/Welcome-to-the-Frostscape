package frostscape.type;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.*;
import frostscape.time.Stopwatch;
import frostscape.util.StatUtils;
import frostscape.world.meta.Family;
import frostscape.world.meta.stat.FrostStats;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;
import mindustry.type.UnitType.UnitEngine;
import mindustry.world.meta.Stat;

public class HollusUnitType extends UnitType{
    public Family family;

    public HollusUnitType(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.useCategories = true;

        if(family != null){
            StatUtils.addFamilyStats(stats, family);
        }
    }

    public static class ActivationEngine extends UnitEngine {
        public float from, to, threshold, target;

        public ActivationEngine(float x, float y, float radius, float rotation, float from, float to, float threshold, float target){
            super(x, y, radius, rotation);
            this.from = from;
            this.to = to;
            this.threshold = threshold;
            this.target = target;
        }

        public void draw(Unit unit) {
            float activation = Mathf.lerp(from, to, Mathf.clamp(Mathf.maxZero(unit.vel().len2() - threshold) / target, 0, 1));
            float iradius = radius;

            radius *= activation;

            super.draw(unit);

            radius = iradius;
        }

    }
}