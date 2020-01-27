package defelo.mc.chunksfall.animations;

import java.util.ArrayList;

public class SpiralAnimation implements IAnimation {
    private ArrayList<int[]> coords;

    public SpiralAnimation() {
        coords = new ArrayList<>();
        for (int k = 0; k < 8; k++) {
            for (int i = k; i <= 15 - k; i++) coords.add(new int[]{i, k});
            for (int i = k + 1; i <= 15 - k; i++) coords.add(new int[]{15 - k, i});
            for (int i = 15 - k - 1; i >= k; i--) coords.add(new int[]{i, 15 - k});
            for (int i = 15 - k - 1; i >= k + 1; i--) coords.add(new int[]{k, i});
        }
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
