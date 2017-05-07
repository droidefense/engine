package droidefense.sdk.system;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sergio on 6/3/16.
 */
public class SystemCallReturn implements Serializable {

    private final ArrayList<String> answer, error, command;

    public SystemCallReturn() {
        answer = new ArrayList<>();
        error = new ArrayList<>();
        command = new ArrayList<>();
    }

    public ArrayList<String> getAnswer() {
        return answer;
    }

    public void addAnswer(String answer) {
        this.addValue(this.answer, answer);
    }

    public ArrayList<String> getError() {
        return error;
    }

    public void addError(String error) {
        this.addValue(this.error, error);
    }

    public ArrayList<String> getCommand() {
        return command;
    }

    public void addCommand(String command) {
        this.addValue(this.command, command);
    }

    private void addValue(ArrayList<String> list, String data) {
        if (data != null && data.length() > 0 && list != null)
            list.add(data);
    }
}
