import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) throws Exception
    {
        // JSON string representing an array of user objects
        String jsonString = "[{\"name\":\"Ram\",\"age\":30},{\"name\":\"Sita\",\"age\":25}]";

        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // De-serialize the JSON string into an array of User objects
        User[] people = objectMapper.readValue(jsonString, User[].class);

        // Print the details of each user
        for (User user : people) {
            System.out.println("Name: " + user.getName() + ", Age: " + user.getAge());
        }
    }
}