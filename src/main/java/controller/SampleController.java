package controller;

import org.neo4j.driver.v1.types.Node;
import org.neo4j.unsafe.impl.batchimport.stats.Stat;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.RequestMethod;

import org.neo4j.driver.v1.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.v1.Values.parameters;
import controller.Train;

@RestController
@EnableAutoConfiguration
public class SampleController {

    static Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "4jneo" ) );
    static Session session = driver.session();


    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";

    }



    @RequestMapping("/station")
    @ResponseBody
    String getStation(){
        StatementResult result = session.run( "MATCH (station:Station) WHERE station.code = {code} " +
                        "RETURN station.name AS name, station.code AS code",
                parameters( "code", "BBS" ) );
        while ( result.hasNext() )
        {
            System.out.println("Reached Inside");
            Record record = result.next();
            System.out.println( record.get( "code" ).asString() + " " + record.get( "name" ).asString() );
        }

        System.out.println("Code is completed");

        return "This is station" ;
    }

    @RequestMapping("/train")
    @ResponseBody
    String getTrain(){


        StatementResult result = session.run( "MATCH (train:Train) WHERE train.num = {num} " +
                        "RETURN train.name AS name, train.num AS num",
                parameters( "num", "'01656'" ) );
        while ( result.hasNext() )
        {
            System.out.println("Reached Inside");
            Record record = result.next();
            System.out.println( record.get( "num" ).asString() + " " + record.get( "name" ).asString() );
        }

        System.out.println("Code is completed");

        return "This is train by Id method" ;
    }


    @RequestMapping(value="/train/{id}", method = RequestMethod.GET)
    @ResponseBody
    Train getTrainbyId(@PathVariable String id){


        Train train;
        train = new Train();
        String num="'"+id+"'";
        Map<String ,Object> params=new HashMap();
        params.put("num",num);
        String query="MATCH (train:Train) WHERE train.num = {num} RETURN train.name AS name, train.num AS num";

        StatementResult result = session.run( query,params );

        while ( result.hasNext() )
        {
            System.out.println("Reached Inside");
            Record record = result.next();
            System.out.println( record.get( "num" ).asString() + " " + record.get( "name" ).asString() );
            train.setNum(record.get( "num" ).asString());
            train.setName(record.get( "name" ).asString());
        }

        System.out.println("Code is completed");

        return train;
    }


    @RequestMapping(value="/train/{id}/stations", method = RequestMethod.GET)
    @ResponseBody
    List<Station> getStationsbyTrainId(@PathVariable String id){


        List<Station> station_list=new ArrayList();


        String num="'"+id+"'";

        Map<String ,Object> params=new HashMap();
        params.put("num",num);

        String query="MATCH (train:Train)-[:INTERMEDIATE]->(s:Station)  WHERE train.num = {num} RETURN s";

        StatementResult result = session.run( query,params );



        while ( result.hasNext() )
        {
            Record record = result.next();
            Node node=(Node)record.asMap().get("s");
            System.out.println("Code:"+node.get("code").toString());
            System.out.println("Name:"+node.get("name"));

            Station station =new Station();
            station.setCode(node.get( "code" ).toString());
            station.setName(node.get( "name" ).toString());
            station_list.add(station);

        }

        System.out.println("Code is completed");

        return station_list ;
    }



    @RequestMapping(value="/author/familyName/{familyName}/givenName/{givenName}/works", method = RequestMethod.GET)
    @ResponseBody
    List<Work> getWorksbyAuthorName(@PathVariable String familyName,@PathVariable String givenName){


        List<Work> works_list=new ArrayList<Work>();


        Map<String ,Object> params=new HashMap();
        params.put("familyName",familyName);
        params.put("givenName",givenName);

        String query="MATCH (contributor:Contributor)-[:AUTHOR]->(work:WORK) WHERE contributor.givenName = {givenName}  and contributor.familyName={familyName}" +
                " RETURN work";


        StatementResult result = session.run( query,params );

        while ( result.hasNext() )
        {

            Record record = result.next();
            Node node=(Node)record.asMap().get("work");

            Work work=new Work();
            work.setDOI(node.get( "DOI" ).toString());

            works_list.add(work);
        }

        System.out.println("Code is completed");

        return works_list;
    }


    @RequestMapping(value="/author/familyName/{familyName}/givenName/{givenName}", method = RequestMethod.GET)
    @ResponseBody
    Author getAuthorbyName(@PathVariable String familyName,@PathVariable String givenName){


        Author author;
        author = new Author();
        Map<String ,Object> params=new HashMap();
        params.put("familyName",familyName);
        params.put("givenName",givenName);

        String query="MATCH (contributor:Contributor) WHERE contributor.givenName = {givenName}  and contributor.familyName={familyName}" +
                " RETURN contributor";


        StatementResult result = session.run( query,params );

        while ( result.hasNext() )
        {

            Record record = result.next();
            Node node=(Node)record.asMap().get("contributor");



            author.setFamilyName(node.get( "familyName" ).toString());
            author.setGivenName(node.get( "givenName" ).toString());
        }

        System.out.println("Code is completed");

        return author;
    }




    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleController.class, args);
    }
}