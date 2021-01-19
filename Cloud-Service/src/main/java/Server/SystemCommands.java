package Server;

public enum SystemCommands {
    ls ("ls"), cdPath ("cd"), catFilePath("cat"), touchFileName ("touch"), mkdirDirName ("mkdir")
    ;

    SystemCommands(String code) {
        this.code = code;
    }
    private String code;

    public String getCode() {
        return code;
    }
}
