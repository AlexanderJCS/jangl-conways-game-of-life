public enum GameMode {
    PLAYING, PAUSED;

    GameMode toggle() {
        if (this.equals(PLAYING)) {
            return PAUSED;
        } else {
            return PLAYING;
        }
    }
}
