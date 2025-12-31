<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>gr.unipi.meallab</groupId>
    <artifactId>meal-lab-app</artifactId>
    <version>1.0.0</version>

    <properties>
        <java.version>21</java.version>
        <javafx.version>21</javafx.version>
    </properties>

    <dependencies>
<dependency>
    <groupId>unipi.MealLab</groupId>
    <artifactId>MealLabAPI</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>


        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        
    </dependencies>

    <build>
        <plugins>
        
                <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.13.0</version>
            <configuration>
                <source>21</source>
                <target>21</target>
            </configuration>
        </plugin>
        
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <mainClass>gr.unipi.meallab.Main</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
