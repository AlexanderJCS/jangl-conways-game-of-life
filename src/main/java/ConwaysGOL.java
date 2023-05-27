import jangl.JANGL;
import jangl.coords.ScreenCoords;
import jangl.graphics.font.Text;
import jangl.graphics.font.parser.Font;
import jangl.io.Window;
import jangl.io.keyboard.KeyEvent;
import jangl.io.keyboard.Keyboard;
import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;
import jangl.time.Clock;
import org.lwjgl.glfw.GLFW;

public class ConwaysGOL implements AutoCloseable {
    private final Cells cells;
    private GameMode mode;
    private final Text instructions;

    public ConwaysGOL() {
        this.cells = new Cells(30, 30, 0.2);
        this.mode = GameMode.PAUSED;

        Font font = new Font(
                "src/main/resources/arial/arial.fnt",
                "src/main/resources/arial/arial.png"
        );

        this.instructions = new Text(new ScreenCoords(-0.99f, 0.99f), font, 0.05f,
                "Click to change a cell state. Press space to toggle the sim.");
    }

    private void processMouseInput() {
        for (MouseEvent event : Mouse.getEvents()) {
            if (event.action != GLFW.GLFW_PRESS) {
                continue;
            }

            this.cells.getCellAt(Mouse.getMousePos()).toggleLiving();
        }
    }

    private void update() {
        // Toggle game modes with a space bar press
        for (KeyEvent event : Keyboard.getEvents()) {
            if (event.key == ' ' && event.action == GLFW.GLFW_PRESS) {
                this.mode = this.mode.toggle();
                this.cells.setPlaying(this.mode == GameMode.PLAYING);
            }
        }

        this.processMouseInput();

        if (this.mode == GameMode.PLAYING) {
            this.cells.update();
            Mouse.getEvents();  // clear the mouse events buffer, so they're not piling up when you go in edit mode
        }
    }

    private void draw() {
        Window.clear();

        this.cells.draw();
        this.instructions.draw();
    }

    public void run() {
        Window.setClearColor(0.1f, 0.1f, 0.1f, 1);

        while (Window.shouldRun()) {
            this.update();
            this.draw();

            JANGL.update();

            try {
                Clock.smartTick(60);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void close() {
        this.cells.close();
        this.instructions.getFont().close();
        this.instructions.close();
    }

    public static void main(String[] args) {
        JANGL.init(1200, 1200);

        ConwaysGOL conwaysGOL = new ConwaysGOL();
        conwaysGOL.run();
        conwaysGOL.close();

        Window.close();
    }
}
