import jangl.coords.PixelCoords;
import jangl.coords.ScreenCoords;
import jangl.io.Window;
import jangl.shapes.Rect;
import jangl.time.Clock;

import java.util.ArrayList;
import java.util.List;

public class Cells implements AutoCloseable {
    private final double updateTime;
    private double timeToUpdate;
    private final Cell[][] cells;

    public Cells(int width, int height, double updateTime) {
        this.updateTime = updateTime;
        this.timeToUpdate = 0;

        this.cells = new Cell[height][width];
        this.generateCells(new ScreenCoords(-1, -1));
    }

    private void generateCells(ScreenCoords bottomLeft) {
        float xScreenDistPerBox = 2f / this.cells.length;
        float yScreenDistPerBox = 2f / this.cells[0].length;

        float xSpace = PixelCoords.distXtoScreenDist(2);
        float ySpace = PixelCoords.distYtoScreenDist(2);

        // Generate the boxes
        for (int h = 0; h < this.cells.length; h++) {
            for (int w = 0; w < this.cells[h].length; w++) {
                Rect rect = new Rect(
                        new ScreenCoords(
                                xScreenDistPerBox * w + bottomLeft.x,
                                yScreenDistPerBox * h + yScreenDistPerBox + bottomLeft.y
                        ),
                        xScreenDistPerBox - xSpace,
                        yScreenDistPerBox - ySpace
                );

                this.cells[h][w] = new Cell(rect);
            }
        }
    }

    public int aliveNeighbors(int x, int y) {
        int aliveNeighbors = 0;

        for (int r = y - 1; r <= y + 1; r++) {
            for (int c = x - 1; c <= x + 1; c++) {
                // Avoid an IndexOutOfBounds exception
                if (r < 0 || r >= this.cells.length || c < 0 || c >= this.cells[r].length) {
                    continue;
                }

                if (cells[r][c].isAlive()) {
                    aliveNeighbors++;
                }
            }
        }

        // subtract by 1 since it counts its own cell as a neighbor
        if (this.cells[y][x].isAlive()) {
            aliveNeighbors--;
        }

        return aliveNeighbors;
    }

    private void updateBoard() {
        List<Cell> cellsToDie = new ArrayList<>();
        List<Cell> cellsToBirth = new ArrayList<>();

        for (int y = 0; y < this.cells.length; y++) {
            for (int x = 0; x < this.cells[y].length; x++) {
                int aliveNeighbors = this.aliveNeighbors(x, y);

                // Any dead cell that has 3 live neighbors becomes a live cell
                if (!this.cells[y][x].isAlive() && aliveNeighbors == 3) {
                    cellsToBirth.add(this.cells[y][x]);
                }

                // Any live cell that doesn't have two or three neighbors dies the next generation
                if (this.cells[y][x].isAlive() && !(aliveNeighbors == 2 || aliveNeighbors == 3)) {
                    cellsToDie.add(this.cells[y][x]);
                }
            }
        }

        for (Cell cell : cellsToDie) {
            cell.die();
        }

        for (Cell cell : cellsToBirth) {
            cell.live();
        }
    }

    public void update() {
        this.timeToUpdate += Clock.getTimeDelta();

        while (this.timeToUpdate > updateTime) {
            this.timeToUpdate -= updateTime;
            this.updateBoard();
        }
    }

    /**
     * Get the cell at the specified screen coordinates.
     *
     * @param screenCoords The screen coords.
     * @return The cell at the screen coordinates.
     */
    public Cell getCellAt(ScreenCoords screenCoords) {
        PixelCoords pixelCoords = screenCoords.toPixelCoords();
        int x = (int) (pixelCoords.x / Window.getScreenWidth() * this.cells[0].length);
        int y = (int) (pixelCoords.y / Window.getScreenHeight() * this.cells.length);

        return cells[y][x];
    }

    public void setPlaying(boolean playing) {
        for (Cell[] row : this.cells) {
            for (Cell cell : row) {
                cell.setPlaying(playing);
            }
        }
    }

    public void draw() {
        for (Cell[] row : this.cells) {
            for (Cell cell : row) {
                cell.draw();
            }
        }
    }

    @Override
    public void close() {
        for (Cell[] row : this.cells) {
            for (Cell cell : row) {
                cell.close();
            }
        }
    }
}
