package util;


public interface EventListener<T> {
    void onEvent(T data);
    //void onLog(LogStage logStage, String... message);
}
