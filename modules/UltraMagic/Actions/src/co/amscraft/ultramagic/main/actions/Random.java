package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.ParentAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Random extends ParentAction {
    @FieldDescription(help = "A map defining probabilities. Key refers to the action #, value refers to weighting")
    public Map<Integer, Integer> chance = new HashMap<>();

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        int sum = sum();
        int index = 0;
        if (sum > 0) {
            double[] map = new double[actions.size()];
            for (int key : keySet()) {
                map[key] = (chance.get(key) * 100.0) / sum;
                //System.out.println(key + ": " + map[key]);
            }
            double random = Math.random() * 100;
            int total = 0;
            for (int i = 0; i < actions.size(); i++) {
                if (random >= total && random <= total + map[i]) {
                    index = i;
                    break;
                }
                total += map[i];
            }
        } else {
            index = (int) (Math.random() * actions.size());
        }
        if (index < this.actions.size()) {
            this.actions.get(index).run(spell, target, caster);
        }
    }

    private int sum() {
        int s = 0;
        for (int key : keySet()) {
            s += chance.get(key);
        }
        return s;
    }

    public List<Integer> keySet() {
        List<Integer> list = new ArrayList<Integer>();
        for (int key : chance.keySet()) {
            if (key < actions.size()) {
                list.add(key);
            }
        }
        return list;
    }
}
