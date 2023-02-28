package AutomatedTimeTableScheduler.CreatePDF;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;

public class AddImage {
    public static void addLogo(Document document) throws Exception{
        Image logo = Image.getInstance("./Images/College Logo.png");
        logo.setAlignment(Element.ALIGN_CENTER);
        logo.scaleAbsoluteWidth(500);
        logo.scaleAbsoluteHeight(50);
        document.add(logo);
    }
}
