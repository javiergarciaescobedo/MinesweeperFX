package es.javiergarciaescobedo.minesweeperfx;

public class MineCell {
 
    public static final byte STATUS_HIDE = 0; // Cell is hidden
    public static final byte STATUS_SHOW = 1; // User has discovered the cell
    public static final byte STATUS_FLAG = 2; // Cell has been marked with a flag
        
    private boolean mine;       // Is or isn't a mine
    private byte minesAround;   // Number of mines around this cell
    private byte status;     // Hidden, shown o flagged

    public MineCell() {
        mine = false;
        minesAround = 0;
        status = STATUS_HIDE;
    }
    
    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public byte getMinesAround() {
        return minesAround;
    }

    public void setMinesAround(byte minesAround) {
        this.minesAround = minesAround;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }
    
}
