import java.util.ArrayList;
import java.util.List;

public class Data {

    enum Names {
        Petrushka("Петрушка"),
        Vishenka("Вишенка"),
        Garden("парк"),
        Prison("тюрьма"),
        Tree("тенистое дерево"),
        GrapeVodka("бутылочка виноградной водки"),
        Inscription("надписи");

        private final String nameData;

        Names(String nameData) {
            this.nameData = nameData;
        }

        public String getNameData() {
            return nameData;
        }
    }

    enum Types {
        Senior("синьор"),
        None("");

        private final String typeData;

        Types(String typeData) {
            this.typeData = typeData;
        }

        public String getTypeData() {
            return typeData;
        }
    }

    static ArrayList<String> text = new ArrayList<>(
            List.of(
                    "к счастью, иной раз",
                    "случалось немного",
                    "в эти редкие минуты",
                    "однако",
                    "и тут",
                    "о себе",
                    "повсюду",
                    "его",
                    "это давало ему возможность",
                    "лишний часок",
                    "он",
                    "что его воспитанник",
                    "и"
                    )
    );
}