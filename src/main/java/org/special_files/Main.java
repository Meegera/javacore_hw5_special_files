package org.special_files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        //task 1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeToString(json, "data.json");

        //task 2
        List<Employee> list_1 = parseXML("data.xml");
        writeToString(listToJson(list_1), "data1.json");

        //task 3
        String newJson = readString("data.json");
        List<Employee> list_2 = jsonToList(newJson);
        list_2.forEach(System.out::println);
    }

    public static List<Employee> parseCSV(String[] column, String fileName){
        try (CSVReader reader = new CSVReader(new FileReader(fileName))){
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(column);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> result = csv.parse();

            return result;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String listToJson(List<Employee> list){
        Type listType = new TypeToken<List<Employee>>() {}.getType();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        String json = gson.toJson(list, listType);

        return json;
    }

    public static void writeToString(String json, String fileName){
        try(FileWriter writer = new FileWriter(fileName)){
            writer.write(json);
            writer.flush();
        } catch (IOException ex){
            ex.getMessage();
        }
    }

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employees = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();


        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element employee = (Element) node;
                Employee newEmployee = new Employee(Long.parseLong(employee.getElementsByTagName("id").item(0).getTextContent()),
                        employee.getElementsByTagName("firstName").item(0).getTextContent(),
                        employee.getElementsByTagName("lastName").item(0).getTextContent(),
                        employee.getElementsByTagName("country").item(0).getTextContent(),
                        Integer.parseInt(employee.getElementsByTagName("age").item(0).getTextContent()));
                employees.add(newEmployee);
            }

        }
        return employees;
    }

    public static String readString(String fileName){
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String result = "";
            String s;
            while ((s = br.readLine()) != null){
                result += s;
            }
            return result;
        } catch (IOException e) {
            e.getMessage();
        }
        return null;
    }

    public static List<Employee> jsonToList(String json){
        List<Employee> result = new ArrayList<>();
        try {
            Object obj = new JSONParser().parse(json);
            JSONArray jsonArray = (JSONArray) obj;

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for(int i =0; i<jsonArray.size(); i++){
                Employee newEmployee = gson.fromJson(jsonArray.get(i).toString(), Employee.class);
                result.add(newEmployee);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}