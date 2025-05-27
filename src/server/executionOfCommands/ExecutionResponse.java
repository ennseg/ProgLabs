package server.executionOfCommands;

/**
 * The type Execution response.
 */
public class ExecutionResponse {
    private boolean exitCode;
    private String message;
    private Object responseObj;

    /**
     * Instantiates a new Execution response.
     *
     * @param code        the code
     * @param message     the message
     * @param responseObj the response obj
     */
    public ExecutionResponse(boolean code, String message, Object responseObj) {
        this.exitCode = code;
        this.message = message;
        this.responseObj = responseObj;
    }

    /**
     * Instantiates a new Execution response.
     *
     * @param code    the code
     * @param message the message
     */
    public ExecutionResponse(boolean code, String message) {
        this(code, message, null);
    }

    /**
     * Instantiates a new Execution response.
     *
     * @param message the message
     */
    public ExecutionResponse(String message) {
        this(true, message, null);
    }

    /**
     * Gets exit code.
     *
     * @return the exit code
     */
    public boolean getExitCode() { return exitCode; }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() { return message; }

    /**
     * Gets response obj.
     *
     * @return the response obj
     */
    public Object getResponseObj() { return responseObj; }

    @Override
    public String toString() {
        return String.valueOf(exitCode) + ";" + message + ";" +
                (responseObj == null ? "null" : responseObj.toString());
    }
}