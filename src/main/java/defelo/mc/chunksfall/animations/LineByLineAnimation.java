package defelo.mc.chunksfall.animations;

public class LineByLineAnimation implements IAnimation {

    private int x = 0;
    private int z = 0;
    private int inc = 1;

    @Override
    public int[] get_column() {
        return new int[]{x, z};
    }

    @Override
    public boolean next() {
        x += inc;
        if (x == 16 || x == -1) {
            if (++z == 16) return false;
            inc *= -1;
            x += inc;
        }
        return true;
    }

}
