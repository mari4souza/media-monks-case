import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AlterarArquivoXML {
    public static void main(String[] args) {
        try {
            // Carregando o arquivo XML
            File inputFile = new File("feed_psel.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // Removendo os produtos fora de estoque
            NodeList itemList = doc.getElementsByTagName("item");
            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                String availability = item.getElementsByTagName("availability").item(0).getTextContent();
                if (availability.contains("Fora de estoque")) {
                    item.getParentNode().removeChild(item);
                }
            }

            // Adicionando cor aos produtos com o atributo color como 'null'
            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                Element colorElement = (Element) item.getElementsByTagName("color").item(0);
                if ("null".equals(colorElement.getTextContent())) {
                    String title = item.getElementsByTagName("title").item(0).getTextContent();
                    String[] parts = title.split("-");
                    if (parts.length > 1) {
                        String color = parts[1].trim().replaceAll("\"", "");
                        colorElement.setTextContent(color);
                    }
                }
            }

            // Corrigindo os links das imagens
            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                Element imageLinkElement = (Element) item.getElementsByTagName("image_link").item(0);
                String imageLink = imageLinkElement.getTextContent();
                if (imageLink.contains(".mp3")) {
                    imageLinkElement.setTextContent(imageLink.replace(".mp3", ".jpg"));
                }
            }

            // Salvando as alterações de volta no arquivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(inputFile);
            transformer.transform(source, result);
            System.out.println("Alterações salvas com sucesso no arquivo XML.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
