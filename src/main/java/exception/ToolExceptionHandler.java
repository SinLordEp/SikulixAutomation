package exception;

import util.DialogUtils;

import java.util.Map;

public class ToolExceptionHandler implements ExceptionHandler {
    private static final Map<Class<? extends Exception>, String> exceptionMessages = Map.of(
            ImageIOException.class,"Failed to read or write image.",
            OperationCancelException.class, "Operation cancelled.",
            TestStepFailedException.class, "Current step failed.",
            UndefinedException.class, "Undefined exception has occurred.",
            FileIOException.class, "Failed to read or write file.",
            UnknownElementException.class, "Not supported step element was found.",
            WindowErrorException.class, "Failed to manage target window frame.");

    @Override
    public <T> T run(ExceptionWithReturn<T> function, String className, String... text) {
        try{
            return function.run();
        }catch(Exception e){
            handleException(e, className, text);
        }
        return null;
    }

    @Override
    public void run(ExceptionWithoutReturn function, String className, String... text) {
        try{
            function.run();
        }catch(Exception e){
            handleException(e, className, text);
        }
    }

    @Override
    public void handleException(Exception e, String className, String... text) {
        if(e instanceof  OperationCancelException){
            return;
        }
        DialogUtils.showErrorDialog(null, "Error", exceptionMessages.getOrDefault(e.getClass(), e.getLocalizedMessage()) + "\n" + e.getMessage());
    }
}
