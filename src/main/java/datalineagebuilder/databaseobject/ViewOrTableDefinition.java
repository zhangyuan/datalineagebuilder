package datalineagebuilder.databaseobject;

public class ViewOrTableDefinition {
    String name;
    String type;
    String definition;

    public String getType() {
        return type;
    }

    public ViewOrTableDefinition(String name, String definition, String type) {
        this.name = name;
        this.definition = definition;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDefinition() {
        return definition;
    }
}
