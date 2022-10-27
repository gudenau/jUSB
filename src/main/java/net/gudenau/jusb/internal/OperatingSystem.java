package net.gudenau.jusb.internal;

import java.util.function.Predicate;

public enum OperatingSystem {
    LINUX((name) -> name.contains("linux"), ".so"),
    WINDOWS((name) -> name.contains("windows"), ".dll"),
    OSX((name) -> name.contains("mac"), ".dylib"),
    UNKNOWN((name) -> false, null),
    ;
    
    private final Predicate<String> predicate;
    private final String extension;
    
    OperatingSystem(Predicate<String> predicate, String extension) {
        this.predicate = predicate;
        this.extension = extension;
    }
    
    public static final OperatingSystem OPERATING_SYSTEM;
    static {
        var os = UNKNOWN;
        var name = System.getProperty("os.name").toLowerCase();
        for(var value : values()) {
            if(value.predicate.test(name)) {
                os = value;
                break;
            }
        }
        OPERATING_SYSTEM = os;
    }
    
    public String extension() {
        return extension;
    }
}
