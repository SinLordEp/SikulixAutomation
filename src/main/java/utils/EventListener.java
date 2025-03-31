package utils;


public interface EventListener<T> {
    void onEvent(T data);
    //void onLog(LogStage logStage, String... message);
}
