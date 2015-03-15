package com.empcraft.psg.jnbt;

/**
 * The {@code TAG_Byte} tag.
 */
public final class ByteTag extends Tag {
    
    private final byte value;
    
    /**
     * Creates the tag with an empty name.
     *
     * @param value the value of the tag
     */
    public ByteTag(final byte value) {
        super();
        this.value = value;
    }
    
    /**
     * Creates the tag.
     *
     * @param name  the name of the tag
     * @param value the value of the tag
     */
    public ByteTag(final String name, final byte value) {
        super(name);
        this.value = value;
    }
    
    @Override
    public Byte getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        final String name = getName();
        String append = "";
        if ((name != null) && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }
        return "TAG_Byte" + append + ": " + this.value;
    }
    
}
