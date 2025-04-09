package data.model;

import java.awt.datatransfer.*;
import javax.swing.*;

public class JListDragActionHandler extends TransferHandler {
    private int oldIndex = -1;
    private OrderChangeListener orderChangeListener;

    public void setOrderChangeListener(OrderChangeListener listener) {
        this.orderChangeListener = listener;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }
    @Override
    protected Transferable createTransferable(JComponent c) {
        // Get original index
        JList<?> list = (JList<?>) c;
        oldIndex = list.getSelectedIndex();
        // return empty to fit API
        return new StringSelection("");
    }

    @Override
    public boolean canImport(TransferSupport support) {
        // allow all drop
        return support.isDrop();
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }
        // Get target index
        JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
        int newIndex = dropLocation.getIndex();

        // relocate all index
        if (oldIndex < newIndex) {
            newIndex--;
        }

        if (orderChangeListener != null) {
            orderChangeListener.orderChanged(oldIndex, newIndex);
        }

        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable t, int action) {
        oldIndex = -1;
    }
}
