package Ex03.GameEngine;

public class XMLInvalidInputException extends  Exception {
    private  String m_Message;

    public XMLInvalidInputException(String i_Message) {
        super(i_Message);
        this.m_Message = i_Message;
    }
}
