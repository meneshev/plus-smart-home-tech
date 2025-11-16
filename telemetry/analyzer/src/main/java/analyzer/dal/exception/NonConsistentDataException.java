package analyzer.dal.exception;

/*
    Используется когда сервис не готов обработать полученные данные, чтобы повторно не получать аналогичное сообщение
    из очереди
 */
public class NonConsistentDataException extends RuntimeException {
    public NonConsistentDataException(String message) {
        super(message);
    }
}
