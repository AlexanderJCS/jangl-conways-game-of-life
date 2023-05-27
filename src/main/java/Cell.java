import jangl.graphics.shaders.ColorShader;
import jangl.shapes.Rect;

public class Cell implements AutoCloseable {
    private final Rect rect;
    private final ColorShader color;
    private boolean alive;
    private boolean playing;

    public Cell(Rect rect) {
        this.rect = rect;
        this.color = new ColorShader(0, 0, 0, 1);

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

    public void calculateColor() {
        if (!this.alive) {  // set to gray if not alive
            this.color.setRGBA(0.2f, 0.2f, 0.2f, 1);
        } else if (this.playing) {  // set to green if alive and playing
            this.color.setRGBA(0, 0.8f, 0, 1);
        } else {  // set to red if alive and not playing
            this.color.setRGBA(0.8f, 0, 0, 1);
        }
    }

    public void live() {
        this.alive = true;
        this.calculateColor();
    }

    public void die() {
        this.alive = false;
        this.calculateColor();
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
        this.calculateColor();
    }

    public void draw() {
        this.rect.draw(this.color);
    }

    @Override
    public void close() {
        this.rect.close();
        this.color.close();
    }
}
