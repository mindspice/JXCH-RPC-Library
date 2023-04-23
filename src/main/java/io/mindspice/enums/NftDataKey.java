package io.mindspice.enums;

public enum NftDataKey {
    DATA("u"),
    LICENSE("mu"),
    METADATA("lu");

    public final String key;

    NftDataKey(String key) { this.key = key; }
}
