package data.model;

public interface OrderChangeListener {
    /**
     * @param oldIndex index before drag action
     * @param newIndex index after drag action
     */
    void orderChanged(int oldIndex, int newIndex);
}
