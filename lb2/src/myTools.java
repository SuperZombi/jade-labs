import java.util.HashMap;

class GameElement {
    String name;

    GameElement(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return Character.toString(name.charAt(0));
    }
}

class Hero extends GameElement {
    String view_direction;
    Hero(String view_direction) {
        super("Hero");
        this.view_direction = view_direction.toLowerCase();
    }

    int[] moveFoward(int y, int x){
        if (this.view_direction == "l"){
            return new int[]{y, x - 1};
        }
        else if (this.view_direction == "r"){
            return new int[]{y, x + 1};
        }
        else if (this.view_direction == "u"){
            return new int[]{y - 1, x};
        }
        else if (this.view_direction == "d"){
            return new int[]{y + 1, x};
        }
        return new int[]{y, x};
    }

    @Override
    public String toString() {
        String[] answer = {" ", " ", " "};
        answer[1]=super.toString();
        if (this.view_direction == "l"){answer[0]="<";}
        else if (this.view_direction == "r"){answer[2]=">";}
        else if (this.view_direction == "u"){answer[2]="⭡";}
        else if (this.view_direction == "d"){answer[2]="⭣";}
        return String.join("", answer);
    }
}
class HashMapParser {
    static public HashMap<String, String> parseStringToHashMap(String input) {
        HashMap<String, String> hashMap = new HashMap<>();
        String[] pairs = input.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                hashMap.put(key, value);
            }
        }
        return hashMap;
    }
}