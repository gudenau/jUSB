package net.gudenau.jusb.internal;

import java.util.function.Predicate;

public enum Architecture {
    AMD64((name) -> name.equals("amd64") || name.equals("x86_64") || name.equals("i586")),
    X86((name) -> name.equals("x86") || name.equals("i386")),
    UNKNOWN((name) -> false),
    ;
    
    private final Predicate<String> predicate;
    
    Architecture(Predicate<String> predicate) {
        this.predicate = predicate;
    }
    
    public static final Architecture ARCHITECTURE;
    static {
        var arch = UNKNOWN;
        var name = System.getProperty("os.arch").toLowerCase();
        for(var value : values()) {
            if(value.predicate.test(name)) {
                arch = value;
                break;
            }
        }
        ARCHITECTURE = arch;
    }
}
