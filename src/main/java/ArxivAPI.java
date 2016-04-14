import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dnes on 14/04/16.
 */
public class ArxivAPI {


    private static String API_URL = "http://export.arxiv.org/api/query?";

    public ArxivAPI(){

    }

    private Document getResponse(String query) throws Exception{

        String uri = API_URL + query;

        URL url = new URL(uri);
        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml");

        InputStream xml = connection.getInputStream();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(xml);

        return doc;

    }

    private static void test(Document root){

        NodeList nodeList = root.getElementsByTagName("entry");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                // do something with the current element
                System.out.println(node.getNodeName());
            }
        }

    }


    public static void main(String[] args) throws SAXException, IOException,
            ParserConfigurationException, TransformerException {

        ArxivAPI api = new ArxivAPI();

        try{

            String query = "search_query=cat:astro-ph.GA+AND+submittedDate:[200903192000+TO+200903232000]";
            //String query = args[0];

            Document ret = api.getResponse(query);

            //listNodeValues(ret);

            test(ret);

        }catch (Exception e){
            System.out.println(e.toString());
        }

    }


    public static void listNodeValues(Node root) throws Exception{

        XPathFactory xFactory = XPathFactory.newInstance();
        XPath xPath = xFactory.newXPath();

        XPathExpression xExpress = xPath.compile("/feed/entry");
        NodeList nodes = (NodeList) xExpress.evaluate(root, XPathConstants.NODESET);

        System.out.println("Found " + nodes.getLength() + " entry nodes");

        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);

            NodeList itens = node.getChildNodes();

            for (int i=0; i<itens.getLength();i++){
                if (itens.item(i).getNodeName().equals("id") ||
                        itens.item(i).getNodeName().equals("title") ||
                        itens.item(i).getNodeName().equals("summary")){
                            System.out.print(itens.item(i).getNodeName().toString() + " - ");
                            System.out.println(itens.item(i).getNodeValue().toString());
                            System.out.println("---");
                }
            }

        }

    }


}
