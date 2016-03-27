package es.javiergarciaescobedo.minesweeperfx;

import java.util.Random;
import java.util.logging.Logger;

public class GameEngine {

    private static final Logger LOG = Logger.getLogger(GameEngine.class.getName());
    
    public MineCell[][] mineCells;
    private int cols;
    private int rows;
    
    public static byte COUNT_IF_MINE = 9;

    /**
     * 
     * @param cols Number of columns in game board 
     * @param rows Number of rows in game board 
     * @param minesNumber Number of mines to put in game board
     */
    public GameEngine(int cols, int rows, int minesNumber) {
        this.cols = cols;
        this.rows = rows;
        
        // Check if mines number is greater than cells number
        if(minesNumber > cols * rows) {
            minesNumber = cols * rows;
            LOG.warning("Mines number greater than gameboard size. "
                    + "It has been set to cells number");      
        }
        
        // Set size for array
        mineCells = new MineCell[cols][rows];
        
        // Fill array with MineCells objects
        for(int x = 0; x < cols; x++) {
            for(int y = 0; y < rows; y++) {
                mineCells[x][y] = new MineCell();
            }
        }
        
        // Set mines randomly without control
        Random random = new Random();
        int minesNumberGenerated = 0;
        while(minesNumberGenerated < minesNumber) {
            int col = random.nextInt(cols);
            int row = random.nextInt(rows);
            if(!mineCells[col][row].isMine()) {
                MineCell mineCell = mineCells[col][row];
                mineCell.setMine(true);
                minesNumberGenerated++;
                LOG.finest("Mine created at " + col + ", " + row);   
            } else {
                LOG.finest("Mine repeated at " + col + ", " + row); 
            }
        }
                
        // Counting mines around every cell
        for(int x = 0; x < cols; x++) {
            for(int y = 0; y < rows; y++) {
                byte minesAroundCounter = 0;
                if(mineCells[x][y].isMine()) {
                    minesAroundCounter = COUNT_IF_MINE;
                } else {
                    for(int x1 = x-1; x1 <= x+1; x1++) {
                        for(int y1 = y-1; y1 <= y+1; y1++) {
                            try {
                                if(mineCells[x1][y1].isMine()) {
                                    minesAroundCounter++;
                                }
                            } catch(ArrayIndexOutOfBoundsException ex) {                                
                            }
                        }
                    }
                }
                LOG.finest("Mines around " + x + ", " + y + " = " + minesAroundCounter); 
                mineCells[x][y].setMinesAround(minesAroundCounter);
            }
        }        
        
        LOG.fine(this.toString());        
    }
    
    public boolean discoverCell(int col, int row) {
        LOG.finest("Discovering cell " + col + ", " + row);
        mineCells[col][row].setStatus(MineCell.STATUS_SHOW);
        LOG.fine(this.toString());
        if(mineCells[col][row].isMine()) {
            LOG.finest("Cell discovered is a mine!!!");
            return true;
        } else {
            if(mineCells[col][row].getMinesAround() != 0) {
                LOG.finest("Cell discovered with mines around: " 
                        + mineCells[col][row].getMinesAround());
                return false;
            } else { // Mines around = 0
                //Cell hidden and mines around = 0. Discover cells recursively
                LOG.finest("Cell discovered without mines around: ");
                for(int x1 = col-1; x1 <= col+1; x1++) {
                    for(int y1 = row-1; y1 <= row+1; y1++) {
                        if(x1 != col || y1 != row) {
                            try {
                                if(mineCells[x1][y1].getStatus() == MineCell.STATUS_HIDE) {
                                    this.discoverCell(x1, y1);
                                }
                            } catch(ArrayIndexOutOfBoundsException ex) {
                            }
                        } else {
                            LOG.finest("This is the same cell at " + x1 + ", "+ y1);
                        }
                    }
                }                        
                return false;
            }
        }
    }
    
    /**
     * Set a flag in cell specified by col and row parameters. If there was
     * a flag in that position, the flag is removed and cell status is set to
     * hidden.
     * you can set a flag if cell content is been showed.
     * @param col
     * @param row
     * @return true if flag has been set or false otherwise
     */
    public boolean setFlag(int col, int row) {
        LOG.finest("Setting flag at " + col + ", " + row);
        boolean result = false;
        MineCell mineCell = mineCells[col][row];
        if(mineCell.getStatus() == MineCell.STATUS_SHOW) {
            LOG.finest("Cell discovered, can't be flagged");
            result = false;
        } else {
            if(mineCell.getStatus() == MineCell.STATUS_FLAG) {
                mineCell.setStatus(MineCell.STATUS_HIDE);
                LOG.finest("Cell already flagged. Flag is removed");
                result = false;
            } else {
                mineCell.setStatus(MineCell.STATUS_FLAG);
                LOG.finest("Flag set");
                result = true;
            }
        }
        LOG.fine(this.toString());
        return result;
    }
    
    /**
     * Return flags counter set in game board
     * @return flags counter
     */
    public int getCounterFlags() {
        LOG.finest("Counting flags");
        int counter = 0;
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                MineCell mineCell = mineCells[c][r];
                if(mineCell.getStatus() == MineCell.STATUS_FLAG) {
                    counter++;
                    LOG.finest("Flag (" + counter + ") found at " + c + ", " + r);
                }
            }
        }
        LOG.finest("Flags counter: " + counter);
        return counter;
    }
    
    /**
     * Check if game is over, testing if there aren't cells hidden
     * @return true if game is over, false otherwise
     */
    public boolean isGameOver() {
        LOG.finest("Checking game over");
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                MineCell mineCell = mineCells[c][r];
                if(mineCell.getStatus() == MineCell.STATUS_HIDE) {
                    LOG.finest("Game running. Cell hidden at " + c + ", " + r);
                    return false;
                }
            }
        }
        LOG.finest("Game is over");
        return true;
    }
    
    public String toString() {
        String result = "\nMines\n";
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                MineCell mineCell = mineCells[c][r];
                if(mineCell.isMine()) {
                    result += "*";
                } else {
                    result += ".";
                }
            }
            result += "\n";
        }
        
        result += "\nMines counter\n";
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                MineCell mineCell = mineCells[c][r];
                result += mineCell.getMinesAround();
            }
            result += "\n";
        }
        
        result += "\nMines status\n";
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                MineCell mineCell = mineCells[c][r];
                switch(mineCell.getStatus()) {
                    case MineCell.STATUS_HIDE:
                        result += '#';
                        break;
                    case MineCell.STATUS_SHOW:
                        result += '-';
                        break;
                    case MineCell.STATUS_FLAG:
                        result += 'F';
                        break;
                }
            }
            result += "\n";
        }
        
        result += "\nFlags counter: " + getCounterFlags() + "\n";
        result += "\nGame over: " + isGameOver()+ "\n";
        
        return result;
    }
    
}
