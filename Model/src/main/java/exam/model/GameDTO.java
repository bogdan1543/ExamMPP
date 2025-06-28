package exam.model;

import java.io.Serializable;

public class GameDTO implements Serializable {
    private String alias;
    private int score;
    private float time;

    public GameDTO(String alias, int score, float time) {
        this.alias = alias;
        this.score = score;
        this.time = time;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
