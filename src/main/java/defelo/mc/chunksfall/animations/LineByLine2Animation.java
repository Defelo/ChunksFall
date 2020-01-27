package defelo.mc.chunksfall.animations;

public class LineByLine2Animation implements IAnimation {

    private int x = 0;
    private int z = 0;
    private int inc = 1;
    private boolean toggle = true;

    @Override
    public int[] get_column() {
        if (toggle)
            return new int[]{x, z};
        else
            return new int[]{15 - x, 15 - z};
    }

    @Override
    public boolean next() {
        if (toggle) toggle = false;
        else {
            toggle = true;
            x += inc;
            if (x == 16 || x == -1) {
                if (++z == 8) return false;
                inc *= -1;
                x += inc;
            }
        }
        return true;
    }

}
