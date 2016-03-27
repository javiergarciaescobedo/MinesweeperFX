package es.javiergarciaescobedo.minesweeperfx;

import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GameBoardFX extends HBox {

    public static final int TILE_SIZE = 61;
    public static final int MARGIN = 10;
    public static final int BUTTONS_PANEL_HEIGHT = 70;
    public static final int INFO_PANEL_HEIGHT = 70;
    
    private GridPane gridpane = new GridPane();
    private boolean flaggingMode = false;    
    private GameEngine gameEngine;
    private boolean running;   
    private int rowClick = -1;
    private int colClick = -1;
    private double visualBoundsWidth;
    private double visualBoundsHeight;
    public double gameBoardWidth;
    public double gameBoardHeight;
    // Información de los tamaños de tableros y número de minas obtenido de:
    //  https://es.wikipedia.org/wiki/Buscaminas
    // Número de celdas horizontal, vertical y número de minas por nivel
    private int[][] levelInfo = {
        {8, 8, 10}, {16, 16, 40}, {22, 22, 99}
    };    
    private int level;   // Identificador de nivel actual

    private static final Image IMAGE_U = new Image("tiles/Minesweeper_LAZARUS_61x61_unexplored.png");
    private static final Image IMAGE_F = new Image("tiles/Minesweeper_61x61_flag.png");
    private static final Image IMAGE_FD = new Image("tiles/Minesweeper_61x61_flag_disabled.png");
    private static final Image IMAGE_L1 = new Image("tiles/Minesweeper_level1.png");
    private static final Image IMAGE_L2 = new Image("tiles/Minesweeper_level2.png");
    private static final Image IMAGE_L3 = new Image("tiles/Minesweeper_level3.png");
    private static final Image IMAGE_K = new Image("tiles/Minesweeper_kill.png");
    private static final Image IMAGE_M = new Image("tiles/Minesweeper_LAZARUS_61x61_mine.png");
    private static final Image IMAGE_H = new Image("tiles/Minesweeper_LAZARUS_61x61_mine_hit.png");
    private static final Image IMAGE_0 = new Image("tiles/Minesweeper_LAZARUS_61x61_0.png");
    private static final Image IMAGE_1 = new Image("tiles/Minesweeper_LAZARUS_61x61_1.png");
    private static final Image IMAGE_2 = new Image("tiles/Minesweeper_LAZARUS_61x61_2.png");
    private static final Image IMAGE_3 = new Image("tiles/Minesweeper_LAZARUS_61x61_3.png");
    private static final Image IMAGE_4 = new Image("tiles/Minesweeper_LAZARUS_61x61_4.png");
    private static final Image IMAGE_5 = new Image("tiles/Minesweeper_LAZARUS_61x61_5.png");
    private static final Image IMAGE_6 = new Image("tiles/Minesweeper_LAZARUS_61x61_6.png");
    private static final Image IMAGE_7 = new Image("tiles/Minesweeper_LAZARUS_61x61_7.png");
    private static final Image IMAGE_8 = new Image("tiles/Minesweeper_LAZARUS_61x61_8.png");

    private ImageView imageViewFlagSelector;
    private ImageView imageViewLevel0;
    private ImageView imageViewLevel1;
    private ImageView imageViewLevel2;
    private ImageView imageViewKill;
    private Text labelMinesCounter;
    private HBox hboxButtonsPanel;
    
    private static final Logger LOG = Logger.getLogger(GameBoardFX.class.getName());
                
    public GameBoardFX(double visualBoundsWidth, double visualBoundsHeight) {

        // Guardar tamaño de pantalla para posteriores usos de escalado
        this.visualBoundsWidth = visualBoundsWidth;
        this.visualBoundsHeight = visualBoundsHeight;
        
        // Info panel
        Text labelMines = new Text("Mines: ");
        labelMines.setFont(new Font(36));
        labelMinesCounter = new Text(String.valueOf(levelInfo[0][2]));
        labelMinesCounter.setFont(new Font(36));
        // Botón (imagen) para colocacion de banderas
        imageViewFlagSelector = new ImageView(IMAGE_F);
        imageViewFlagSelector.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                LOG.fine("Flagging mode");
                flaggingMode = true;
                imageViewFlagSelector.setImage(IMAGE_FD);
            }
        });
        // Botones para selección de niveles
        imageViewLevel0 = new ImageView(IMAGE_L1);
        imageViewLevel1 = new ImageView(IMAGE_L2);
        imageViewLevel2 = new ImageView(IMAGE_L3);
        imageViewKill = new ImageView(IMAGE_K);

        imageViewLevel0.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                LOG.fine("Selected level 1");
                initGameBoard(0);
            }
        });
        imageViewLevel1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                LOG.fine("Selected level 2");
                initGameBoard(1);
            }
        });
        imageViewLevel2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                LOG.fine("Selected level 3");
                initGameBoard(2);
            }
        });
        imageViewKill.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                LOG.fine("Selected kill");
                running = false;
                updateTiles();        
            }
        });

        gridpane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(running) {
                    colClick = (int)(event.getX() / TILE_SIZE);
                    rowClick = (int)(event.getY() / TILE_SIZE);
                    switch(event.getButton()) {
                        case PRIMARY:
                            LOG.fine("Right Click on: " + colClick + ", " + rowClick);
                            if(flaggingMode) {
                                gameEngine.setFlag(colClick, rowClick);
                                updateTiles();
                                imageViewFlagSelector.setImage(IMAGE_F);
                                flaggingMode = false;
                            } else {
                                if(gameEngine.discoverCell(colClick, rowClick)) {
                                    running = false;
                                } 
                                updateTiles();                    
                            }
                            break;
                        case SECONDARY:
                            LOG.fine("Left Click on: " + colClick + ", " + rowClick);
                            gameEngine.setFlag(colClick, rowClick);
                            updateTiles();
                            imageViewFlagSelector.setImage(IMAGE_F);
                            flaggingMode = false;
                            break;
                    }
                    if(gameEngine.isGameOver()) {
                        running = false;
                    }
                }
            }
        });

        hboxButtonsPanel = new HBox(imageViewLevel0, imageViewLevel1, 
                imageViewLevel2, imageViewKill, imageViewFlagSelector);
        hboxButtonsPanel.setAlignment(Pos.CENTER);
        hboxButtonsPanel.setSpacing(MARGIN);
        hboxButtonsPanel.setStyle("-fx-padding: " + MARGIN);
        hboxButtonsPanel.setMaxHeight(BUTTONS_PANEL_HEIGHT);
        hboxButtonsPanel.setMinHeight(BUTTONS_PANEL_HEIGHT);
        HBox hboxInfoPanel = new HBox(labelMines, labelMinesCounter);
        hboxInfoPanel.setAlignment(Pos.CENTER);
        hboxInfoPanel.setSpacing(MARGIN);
        hboxInfoPanel.setMaxHeight(INFO_PANEL_HEIGHT);
        hboxInfoPanel.setMinHeight(INFO_PANEL_HEIGHT);
        
        // Main container
        // Truco para que quede ajustado el tablero al escalar sin que deje márgenes
        //  Group > GridPane
        Group group = new Group(gridpane);
        VBox vbox = new VBox(hboxButtonsPanel, hboxInfoPanel, group);
        vbox.setAlignment(Pos.CENTER);

        this.getChildren().add(vbox);
        this.setAlignment(Pos.CENTER);      
        
        // Inicialmente se inicia el juego con el primer nivel
        initGameBoard(0);
        
        running = true;

        gameBoardWidth = levelInfo[0][0] * TILE_SIZE + MARGIN;
        gameBoardHeight = BUTTONS_PANEL_HEIGHT + INFO_PANEL_HEIGHT
                + levelInfo[0][1] * TILE_SIZE + MARGIN;       
    }
    
    private void initGameBoard(int level) {
        this.level = level;
        gameEngine = new GameEngine(levelInfo[level][0], levelInfo[level][1], levelInfo[level][2]);
        running = true;
        // Fill gridpane with imageViews (empty cell images)
        gridpane.getChildren().clear();
        
        for(int r = 0; r < levelInfo[this.level][1]; r++) {
            for(int c = 0; c < levelInfo[this.level][0]; c++) {
                ImageView imageView = new ImageView(IMAGE_U);
//                GridPane.setVgrow(imageView, Priority.ALWAYS);

                gridpane.add(imageView, c, r);
            }
        }
        updateTiles();
        scaleGameBoard();
    }
    
    private void scaleGameBoard() {
        double scaleH = 1;
        double scaleV = 1;
        // Obtener escala para el nivel actual respecto al primer nivel
        scaleH = (double)levelInfo[0][0] / levelInfo[this.level][0];
        scaleV = (double)levelInfo[0][1] / levelInfo[this.level][1];
        LOG.fine("ScaleH: " + scaleH + " ScaleV: " + scaleV);
        // Asignar la escala más pequeña
        if(scaleH < scaleV) {
            LOG.fine("Setting scale: " + scaleH);
            gridpane.setScaleX(scaleH);
            gridpane.setScaleY(scaleH);
        } else {
            LOG.fine("Setting scale: " + scaleV);
            gridpane.setScaleX(scaleV);
            gridpane.setScaleY(scaleV);
        }
    }
    
    private void updateTiles() {
        labelMinesCounter.setText(String.valueOf(levelInfo[level][2] - gameEngine.getCounterFlags()));
                
        for(int r = 0; r < levelInfo[level][1]; r++) {
            for(int c = 0; c < levelInfo[level][0]; c++) {
                // Get imageView reference considering its secuencial order
                ImageView imageView = (ImageView)gridpane.getChildren()
                        .get(r * levelInfo[level][0] + c);
                // Change the image according to cell status
                MineCell mineCell = gameEngine.mineCells[c][r];
                switch(mineCell.getStatus()) {
                    case MineCell.STATUS_HIDE:
                        imageView.setImage(IMAGE_U);
                        break;
                    case MineCell.STATUS_FLAG:
                        imageView.setImage(IMAGE_F);
                        break;
                    case MineCell.STATUS_SHOW:
                        switch(mineCell.getMinesAround()) {
                            case 0:
                                imageView.setImage(IMAGE_0);
                                break;
                            case 1:
                                imageView.setImage(IMAGE_1);
                                break;
                            case 2:
                                imageView.setImage(IMAGE_2);
                                break;
                            case 3:
                                imageView.setImage(IMAGE_3);
                                break;
                            case 4:
                                imageView.setImage(IMAGE_4);
                                break;
                            case 5:
                                imageView.setImage(IMAGE_5);
                                break;
                            case 6:
                                imageView.setImage(IMAGE_6);
                                break;
                            case 7:
                                imageView.setImage(IMAGE_7);
                                break;
                            case 8:
                                imageView.setImage(IMAGE_8);
                                break;
                        }
                        break;
                }
                if(!running && mineCell.isMine()) {
                    if(r == rowClick && c == colClick) {
                        imageView.setImage(IMAGE_H);
                    } else {
                        imageView.setImage(IMAGE_M);
                    }
                }
            }
        }
    }
        
}
