/**
 * Created by Пошка on 28.11.2017.
 */
package hello.reddwarf.server;

public enum Weapon {
    ROCK,
    PAPER,
    SCISSORS;
    boolean beats(Weapon other) {
        return other != null && this != other && this.ordinal() == (other.ordinal() + 1) % values().length;
    }
}