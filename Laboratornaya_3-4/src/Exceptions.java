public class Exceptions {
    static class PersonNotFound extends Exception {
        public PersonNotFound(String message) {
            super(message);
        }

        @Override
        public String getMessage() {
            return "Ошибка: Персонаж не найден! " + super.getMessage();
        }
    }

    static class PlaceNotFound extends Exception {
        public PlaceNotFound(String message) {
            super(message);
        }

        @Override
        public String getMessage() {
            return "Ошибка: Место не найдено! " + super.getMessage();
        }
    }

    static class ItemNotFound extends Exception {
        public ItemNotFound(String message) {
            super(message);
        }

        @Override
        public String getMessage() {
            return "Ошибка: Предмет не найден! " + super.getMessage();
        }
    }

}
