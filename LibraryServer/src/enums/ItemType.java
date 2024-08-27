package enums;

import java.io.Serializable;

public enum ItemType implements Serializable {
    BOOK(0),MOVIE(1),AUDIOBOOK(2),GAME(3),DVD(4);
    private int value;

    ItemType(int value) {
        this.value = value;
    }
}