package enums;
import java.io.Serializable;

public enum Result implements Serializable {
    SUCCESS,NO_ACCOUNT,INCORRECT_PASSWORD,FAILURE,SAME_PASSWORD;

    private static final long serialVersionUID = 9999L;
}
