package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        List<Employee> listFromXML = parseXML("data.xml");
        String json2 = listToJson(listFromXML);
        writeString(json, "data2.json");
    }

    public static List<Employee> parseXML(String filePath) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(filePath);
            Node node = document.getDocumentElement();
            NodeList nodeList = node.getChildNodes();
            List<Employee> employees = new ArrayList<>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) nodeList.item(i);
                    String id = e.getElementsByTagName("id").item(0).getTextContent();
                    String firstName = e.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = e.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = e.getElementsByTagName("country").item(0).getTextContent();
                    String age = e.getElementsByTagName("age").item(0).getTextContent();

                    Employee employee = new Employee(Long.parseLong(id), firstName, lastName, country, Integer.parseInt(age));
                    employees.add(employee);
                }
            }
            return employees;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {   // ИЛИ
//            FileReader reader = new FileReader(fileName); todo
//            CSVReader csvReader = new CSVReader(reader);
            ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBeanBuilder builder = new CsvToBeanBuilder(csvReader);
            CsvToBean csvToBean = builder.withMappingStrategy(strategy).build();
//            List<Employee> list = csvToBean.parse();      todo remove
//                    return list;
            return csvToBean.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String destination) {
        try {
            FileWriter fileWriter = new FileWriter(destination);
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}