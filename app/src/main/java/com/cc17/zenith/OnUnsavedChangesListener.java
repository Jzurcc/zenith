package com.cc17.zenith;

public interface OnUnsavedChangesListener {
    /**
     * Checks if the fragment has unsaved data.
     * @return true if unsafe to exit, false if safe.
     */
    boolean hasUnsavedChanges();

    /**
     * Shows the dialog.
     * @param onConfirm The action to run if the user clicks "Discard Changes".
     */
    void showUnsavedChangesDialog(Runnable onConfirm);
}
