package defelo.mc.chunksfall.animations;

public class LineByLine4Animation implements IAnimation {

    private int x = 0;
    private int z = 0;
    private int inc = 1;
    private int toggle = 0;

    @Override
    public int[] get_column() {
        switch (toggle) {
            case 0:
                return new int[]{x, z};
            case 1:
                return new int[]{15 - z, x};
            case 2:
                return new int[]{15 - x, 15 - z};
            case 3:
            default:
                return new int[]{z, 15 - x};
        }
    }

    @Override
    public boolean next() {
        if (++toggle == 4) {
            toggle = 0;
            x += inc;
            if (x == 8 || x == -1) {
                if (++z == 8) return false;
                inc *= -1;
                x += inc;
            }
        }
        return true;
    }

}
