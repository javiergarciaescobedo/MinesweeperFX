package es.javiergarciaescobedo.minesweeperfx;

import com.gluonhq.charm.down.common.PlatformFactory;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        GameBoardFX gameBoardFX = new GameBoardFX(
                visualBounds.getWidth(), visualBounds.getHeight());
        
        // AJUSTAR ESCALA A TIPO DE PANTALLA
        LOG.fine("Platform: " + PlatformFactory.getPlatform().getName());
        // Ajustar tamaño de escena al tamaño de la pantalla, excepto en Desktop
        //  que se ajustará al tamaño del juego
        double sceneWidth = visualBounds.getWidth();
        double sceneHeight = visualBounds.getHeight();
        // Se utiliza la clase PlatformFactory para detectar el tipo de dispositivo
        //  en el que se esté ejecutando la aplicación. Requiere añadir en
        //  archivo build.gradle la dependencia a la librería Charm Down
        if(PlatformFactory.getPlatform().getName().equals("Desktop")) {
            sceneWidth = gameBoardFX.gameBoardWidth;
            sceneHeight = gameBoardFX.gameBoardHeight;
        } else {
            // Escalar el juego para ajustar a tamaño de pantalla en móviles
            double visualRatio = visualBounds.getWidth() / visualBounds.getHeight();
            double gameBoardRatio = gameBoardFX.gameBoardWidth / gameBoardFX.gameBoardHeight;
            double scale = 1;
            if(visualRatio < gameBoardRatio) {
                // El tablero es más ancho que la pantalla. Ajustar al ancho
                scale = visualBounds.getWidth() / gameBoardFX.gameBoardWidth;
                LOG.fine("Fit to width");
            } else {
                // El tablero es más estrecho que la pantalla. Ajustar al alto
                scale = visualBounds.getHeight() / gameBoardFX.gameBoardHeight;
                LOG.fine("Fit to height");
            }
            LOG.fine("Setting game scale: " + scale);
            gameBoardFX.setScaleX(scale);
            gameBoardFX.setScaleY(scale);            
        }
        
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        
        // En caso de ejecutar en Android, cerrar la aplicación al usar la
        //  tecla de retroceso (se asocia a la tecla Escape)
        if(PlatformFactory.getPlatform().getName().equals("Android")) {
            scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent evt) {
                    if (evt.getCode().equals(KeyCode.ESCAPE)) {
                        Platform.exit();
                    }
                }
            });
        }
        
        // El icono se ha obtenido de https://openclipart.org/detail/20846/cartoon-sea-mine
        // Para que los dispositivos Android y iOS utilicen ese mismo icono, se
        //  reemplazan las imágenes que se encuentran en las carpetas src/android/res y
        //  src/ios/assets. Se pueden usar herramientas como https://makeappicon.com
        //  para generar los archivos correspondientes a cada tamaño
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/icon.png")));

        stage.setScene(scene);
        stage.setTitle("MinesweeperFX");
        stage.show();
        
        root.getChildren().add(gameBoardFX);

    }

    
}
