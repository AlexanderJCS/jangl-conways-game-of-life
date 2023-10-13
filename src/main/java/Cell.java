import jangl.color.ColorFactory;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.ColorShader;
import jangl.shapes.Rect;

public class Cell implements AutoCloseable {
    private static final ShaderProgram COLOR_ALIVE_PLAYING = new ShaderProgram(new ColorShader(ColorFactory.fromNormalized(0, 0.8f, 0, 1)));
    private static final ShaderProgram COLOR_ALIVE_PAUSED = new ShaderProgram(new ColorShader(ColorFactory.fromNormalized(0.9f, 0, 0, 1)));
    private static final ShaderProgram COLOR_DEAD = new ShaderProgram(new ColorShader(ColorFactory.fromNormalized(0.2f, 0.2f, 0.2f, 1)));

    private final Rect rect;
    private boolean alive;
    private boolean playing;

    public Cell(Rect rect) {
        this.rect = rect;
        this.die();
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void toggleLiving() {
        if (this.isAlive()) {
            this.die();
        } else {
            this.live();
        }
    }

    public ShaderProgram calculateColor() {
        if (!this.alive) {  // set to gray if not alive
            return COLOR_DEAD;
        } else if (this.playing) {  // set to green if alive and playing
            return COLOR_ALIVE_PLAYING;
        } else {  // set to red if alive and not playing
            return COLOR_ALIVE_PAUSED;
        }
    }

    public void live() {
        this.alive = true;
    }

    public void die() {
        this.alive = false;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void draw() {
        this.rect.draw(this.calculateColor());
    }

    @Override
    public void close() {
        this.rect.close();
    }
}
