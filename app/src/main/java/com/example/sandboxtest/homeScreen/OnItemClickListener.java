package com.example.sandboxtest.homeScreen;

public interface OnItemClickListener {
    void onItemClick(String packageName);

    void onItemLongClick(String name, String packageName, int pos);
}
