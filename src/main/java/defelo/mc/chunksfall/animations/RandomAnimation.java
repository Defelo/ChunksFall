package defelo.mc.chunksfall.animations;

import java.util.ArrayList;
import java.util.Collections;

public class RandomAnimation implements IAnimation {

    private ArrayList<int[]> coords;

    public RandomAnimation() {
        coords = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                coords.add(new int[]{i, j});
            }
        }
        Collections.shuffle(coords);
    }

    @Override
    public int[] get_column() {
        return coords.get(0);
    }

    @Override
    public boolean next() {
        coords.remove(0);
        return !coords.isEmpty();
    }
}
