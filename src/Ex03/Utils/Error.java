package Ex03.Utils;

public class Error {
    public enum ErrorType {FILE_UPLOAD, NAME_USED, ROOM_NAME_USED, UNEXPECTED}

    private String m_ErrorMsg;
    private ErrorType m_ErrorType;

    public Error(String i_Msg, ErrorType i_ErrorType) {
        m_ErrorMsg = i_Msg;
        m_ErrorType = i_ErrorType;
    }
}
