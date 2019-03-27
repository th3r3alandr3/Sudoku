import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class JTextFieldLimit extends JTextField {

  private int limit;

  JTextFieldLimit(int limit) {
    super();
    this.limit = limit;
  }

  @Override
  protected Document createDefaultModel() {
    return new LimitDocument();
  }

  private class LimitDocument extends PlainDocument {

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
      if (str == null) {
        return;
      }

      if ((getLength() + str.length()) <= limit && str.charAt(0) >= 49 && str.charAt(0) <= 57) {
        super.insertString(offset, str, attr);
      }
    }
  }
}
