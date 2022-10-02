package json4j;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

public class Json4jTest
{
    /**
     * https://www.baeldung.com/java-org-json
     */

    // 4.1. Creating JSON Directly From JSONObject
    @Test
    void json4j_baeldung_41()
    {
        {
            JSONObject jo = new JSONObject();
            jo.put("name", "jon doe");
            jo.put("age", "22");
            jo.put("city", "chicago");

            // notice that the order of members are not defined
            final var expected
            = ""
            + "{"
            + "\"city\":\"chicago\""
            + ","
            + "\"name\":\"jon doe\""
            + ","
            + "\"age\":\"22\""
            + "}"
            ;
            assertEquals(expected, jo.toString());
        }
        {
            JSONObject jo = new JSONObject();
            jo.put("name", "Good Morning, Vietnam");
            jo.put("release", "1987");

            // notice that the order of members are not defined
            final var expected
            = ""
            + "{"
            + "\"release\":\"1987\""
            + ","
            + "\"name\":\"Good Morning, Vietnam\""
            + "}"
            ;
            assertEquals(expected, jo.toString());
        }
    }

    // 4.2. Creating JSON From Map
    @Test
    void json4j_baeldung_42()
    {
        {
            Map<String, String> map = new HashMap<>();
            map.put("name", "jon doe");
            map.put("age", "22");
            map.put("city", "chicago");
            JSONObject jo = new JSONObject(map);

            // notice that the order of members follow the order
            // in which map elements are put
            final var expected
            = ""
            + "{"
            + "\"name\":\"jon doe\","
            + "\"city\":\"chicago\","
            + "\"age\":\"22\""
            + "}"
            ;
            assertEquals(expected, jo.toString());
        }
        {
            Map<String, String> map = new HashMap<>();
            map.put("name", "Good Morning, Vietnam");
            map.put("release", "1987");
            JSONObject jo = new JSONObject(map);

            // notice that the order of members follow the order
            // in which map elements are put
            final var expected
            = ""
            + "{"
            + "\"name\":\"Good Morning, Vietnam\""
            + ","
            + "\"release\":\"1987\""
            + "}"
            ;
            assertEquals(expected, jo.toString());
        }
    }

    // 4.3. Creating JSONObject From JSON String
    @Test
    void json4j_baeldung_43()
    {
        {
            final var source
            = ""
            + "{"
            + "\"city\":\"chicago\""
            + ","
            + "\"name\":\"jon doe\""
            + ","
            + "\"age\":\"22\""
            + "}"
            ;
            JSONObject jo = new JSONObject(source);
    
            // notice that the order of members follow the order
            // in which elements are specified
            final var expected
            = ""
            + "{"
            + "\"city\":\"chicago\""
            + ","
            + "\"name\":\"jon doe\""
            + ","
            + "\"age\":\"22\""
            + "}"
            ;
            assertEquals(expected, jo.toString());    
        }
    }

    // 4.4. Serialize Java Object to JSON
    /**
     * One of JSONObject's constructors takes a POJO as its argument.
     * In the example below, the package uses the getters from the
     * DemoBean class and creates an appropriate JSONObject for the same.
     * 
     * To get a JSONObject from a Java Object, we'll have to use a class
     * that is a valid Java Bean:
     */
    public class DemoBean
    {
        public int getId() { return id; }
        public void setId(final int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(final String name) { this.name = name; }

        public boolean getActive() { return state; }
        public void setActive(final boolean state) { this.state = state; }

        private int id;
        private String name;
        private boolean state;
    }

    /**
     * Although we have a way to serialize a Java object to JSON string,
     * there is no way to convert it back using this library.
     * If we want that kind of flexibility, we can switch to other
     * libraries such as Jackson.
     */
    @Test
    void json4j_baeldung_44()
    {
        {
            DemoBean demo = new DemoBean();
            demo.setId(1);
            demo.setName("lorem ipsum");
            demo.setActive(true);

            JSONObject jo = new JSONObject(demo);

            final String expected
            = ""
            + "{"
            + "\"name\":\"lorem ipsum\""
            + ","
            + "\"active\":true"
            + ","
            + "\"id\":1"
            + "}"
            ;
            assertEquals(expected, jo.toString());    
        }
    }

    // 5.1. Creating JSONArray
    @Test
    void json4j_baeldung_51()
    {
        {
            JSONArray ja = new JSONArray();
            ja.put(Boolean.TRUE);
            ja.put("lorem ipsum");

            JSONObject jo = new JSONObject();
            jo.put("name", "jon doe");
            jo.put("age", "22");
            jo.put("city", "chicago");

            ja.put(jo);

            final String expected
            = ""
            + "["
            + "true"
            + ","
            + "\"lorem ipsum\""
            + ","
            + "{"
            + "\"city\":\"chicago\""
            + ","
            + "\"name\":\"jon doe\""
            + ","
            + "\"age\":\"22\""
            + "}"
            + "]"
            ;
            assertEquals(expected, ja.toString());
        }
    }

    // 5.2. Creating JSONArray Directly From JSON String
    @Test
    void json4j_baeldung_52()
    {
        {
            JSONArray ja
            = new JSONArray("[true, \"lorem ipsum\", 215]");

            final String expected
            = ""
            + "["
            + "true"
            + ","
            + "\"lorem ipsum\""
            + ","
            + "215"
            + "]"
            ;
            assertEquals(expected, ja.toString());
        }
    }

    // 5.3. Creating JSONArray Directly From a Collection or an Array
    @Test
    void json4j_baeldung_53()
    {
        {
            List<String> list = new ArrayList<>();
            list.add("California");
            list.add("Texas");
            list.add("Hawaii");
            list.add("Alaska");

            JSONArray ja = new JSONArray(list);

            // notice that the order of members follow the order
            // in which list elements are put
            final String expected
            = ""
            + "["
            + "\"California\""
            + ","
            + "\"Texas\""
            + ","
            + "\"Hawaii\""
            + ","
            + "\"Alaska\""
            + "]"
            ;
            assertEquals(expected, ja.toString());
        }
    }

    // 7. CDL
    /**
     * We're provided with a CDL (Comma Delimited List) class to
     * convert comma delimited text into a JSONArray and vice versa.
     */

    // 7.1. Producing JSONArray Directly From Comma Delimited Text
    /**
     * In order to produce a JSONArray directly from the comma
     * delimited text, we can use the static method
     * {@code rowToJSONArray()}, which accepts a
     * {@code JSONTokener}.
     */
    @Test
    void json4j_baeldung_71()
    {
        {
            JSONArray ja = CDL.rowToJSONArray(new JSONTokener(
                "England, USA, Canada"
            ));

            final String expected
            = "[\"England\",\"USA\",\"Canada\"]"
            ;
            assertEquals(expected, ja.toString());
        }
        {
            JSONArray ja = CDL.rowToJSONArray(new JSONTokener(
                ""
                + "The Deer Hunter"
                + ", "
                + "\"Good Morning, Vietnam\""
                + ", "
                + "The Bridges of Madison County"
                + ", "
                + "The Intern"
            ));

            final String expected
            = ""
            + "["
            + "\"The Deer Hunter\""
            + ","
            + "\"Good Morning, Vietnam\""
            + ","
            + "\"The Bridges of Madison County\""
            + ","
            + "\"The Intern\""
            + "]"
            ;
            assertEquals(expected, ja.toString());
        }
    }

    // 7.2. Producing Comma Delimited Text From JSONArray
    @Test
    void json4j_baeldung_72()
    {
        {
            final String source
            = "[\"England\",\"USA\",\"Canada\"]"
            ;

            JSONArray ja = new JSONArray(source);

            final String expected
            = "England,USA,Canada"
            + "\n"
            ;
            assertEquals(expected, CDL.rowToString(ja));
        }
        {
            final String source
            = ""
            + "["
            + "\"The Deer Hunter\""
            + ","
            + "\"Good Morning, Vietnam\""
            + ","
            + "\"The Bridges of Madison County\""
            + ","
            + "\"The Intern\""
            + "]"
            ;

            JSONArray ja = new JSONArray(source);

            final String expected
            = ""
            + "The Deer Hunter"
            + ","
            + "\"Good Morning, Vietnam\""
            + ","
            + "The Bridges of Madison County"
            + ","
            + "The Intern"
            + "\n"
            ;
            assertEquals(expected, CDL.rowToString(ja));
        }
    }

    // 7.3. Producing JSONArray of JSONObjects Using Comma Delimited Text
    @Test
    void json4j_baeldung_73()
    {
        final String expected
        = ""
        + "["
            + "{"
                + "\"name\":\"john\""
                + ","
                + "\"city\":\"chicago\""
                + ","
                + "\"age\":\"22\""
            + "}"
            + ","
            + "{"
                + "\"name\":\"gary\""
                + ","
                + "\"city\":\"florida\""
                + ","
                + "\"age\":\"35\""
            + "}"
            + ","
            + "{"
                + "\"name\":\"sal\""
                + ","
                + "\"city\":\"vegas\""
                + ","
                + "\"age\":\"18\""
            + "}"
        + "]"
        ;

        /**
         * To produce a JSONArray of JSONObjects, we'll use a text String
         * containing both headers and data separated by commas.
         * We separate the different lines using a carriage return (\r) or
         * line feed (\n).
         * The first line is interpreted as a list of headers, and all the
         * subsequent lines are treated as data:
         */
        {
            final String source
            = ""
            + "name, city, age \n"
            + "john, chicago, 22 \n"
            + "gary, florida, 35 \n"
            + "sal, vegas, 18";

            JSONArray actual = CDL.toJSONArray(source);
            assertEquals(expected, actual.toString());
        }

        /**
         * We have an alternative way of doing this where we can achieve the
         * same functionality by supplying a JSONArray to get the headers and
         * a comma delimited String working as the data.
         */
        {
            JSONArray ja = new JSONArray();
            ja.put("name");
            ja.put("city");
            ja.put("age");

            final String source
            = ""
            + "john, chicago, 22 \n"
            + "gary, florida, 35 \n"
            + "sal, vegas, 18";

            JSONArray actual = CDL.toJSONArray(ja, source);
            assertEquals(expected, actual.toString());
        }
    }    
}
