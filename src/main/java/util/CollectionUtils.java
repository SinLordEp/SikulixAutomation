package util;

import java.util.List;

public class CollectionUtils {
    private CollectionUtils() {}

    /**
     * Moves the element at the oldIndex in the given List to the newIndex.
     * If oldIndex is before newIndex, the newIndex needs to be adjusted
     * (newIndex - 1) because the list automatically shifts left after removal
     * of the element.
     *
     * @param <T> The type of elements in the list
     * @param list The List to be manipulated (e.g., ArrayList or LinkedList)
     * @param oldIndex The original position of the element
     * @param newIndex The target position to move the element to
     * @throws IllegalArgumentException If the list is null
     * @throws IndexOutOfBoundsException If oldIndex or newIndex are invalid
     */
    public static <T> void moveElementInList(List<T> list, int oldIndex, int newIndex) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }
        if (oldIndex < 0 || oldIndex >= list.size()) {
            throw new IndexOutOfBoundsException("Invalid out of bound oldIndex: " + oldIndex);
        }
        if (newIndex < 0 || newIndex > list.size()) {
            throw new IndexOutOfBoundsException("Invalid out of bound newIndex: " + newIndex);
        }

        if (oldIndex == newIndex) {
            return;
        }

        T element = list.remove(oldIndex);
        list.add(newIndex, element);
    }
}
