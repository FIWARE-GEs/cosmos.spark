### OrionReceiver

-   Import dependency.

```scala
import org.fiware.cosmos.orion.spark.connector.{OrionReceiver}
```

-   Add source to Spark Environment. Indicate what port you want to listen to (e.g. 9001).

```scala

 val sparkConf = new SparkConf().setAppName("CustomReceiver").setMaster("local[3]")
 val ssc = new StreamingContext(sparkConf, Seconds(10))

 val eventStream = ssc.receiverStream(new OrionReceiver(9001))

```

-   Parse the received data.

```scala

 val processedDataStream = eventStream.
        .flatMap(event => event.entities)
        // ...processing

```

The received data is a DataStream of objects of the class **`NgsiEvent v2`**. This class has the following attributes:

-   **`creationTime`**: Timestamp of arrival.

-   **`service`**: FIWARE service extracted from the HTTP headers.

-   **`servicePath`**: FIWARE service path extracted from the HTTP headers.

-   **`entities`**: Sequence of entites included in the message. Each entity has the following attributes:

    -   **`id`**: Identifier of the entity.

    -   **`type`**: Node type.

    -   **`attrs`**: Map of attributes in which the key is the attribute name and the value is an object with the
        following properties:

        -   **`type`**: Type of value (Float, Int,...).

        -   **`value`**: Value of the attribute.

        -   **`metadata`**: Additional metadata.
        
        
### NGSILDReceiver

-   Import dependency.

```scala
import org.fiware.cosmos.orion.spark.connector.{NGSILDReceiver}
```

-   Add source to Spark Environment. Indicate what port you want to listen to (e.g. 9001).

```scala

 val sparkConf = new SparkConf().setAppName("CustomReceiver").setMaster("local[3]")
 val ssc = new StreamingContext(sparkConf, Seconds(10))

 val eventStream = ssc.receiverStream(new NGSILDReceiver(9001))

```

-   Parse the received data.

```scala

 val processedDataStream = eventStream.
        .flatMap(event => event.entities)
        // ...processing

```

The received data is a DataStream of objects of the class **`NgsiEvent LD`**. This class has the following attributes:

-   **`creationTime`**: Timestamp of arrival.

-   **`service`**: FIWARE service extracted from the HTTP headers.

-   **`servicePath`**: FIWARE service path extracted from the HTTP headers.

-   **`entities`**: Sequence of entites included in the message. Each entity has the following attributes:

    -   **`id`**: Identifier of the entity.

    -   **`type`**: Node type.

    -   **`attrs`**: Map of attributes in which the key is the attribute name and the value is an object with the
        following properties:

        -   **`type`**: Type of value (Float, Int,...).

        -   **`value`**: Value of the attribute.

    -   **`@context`**: Map of terms to URIs providing an unambiguous definition.
    
    
### OrionSink

-   Import dependency.

```scala
import org.fiware.cosmos.orion.spark.connector.{OrionSink,OrionSinkObject,ContentType,HTTPMethod}
```

-   Add sink to source.

```scala

   val processedDataStream = eventStream.
    // ... processing
    .map(obj =>
       new OrionSinkObject(
           "{\"temperature_avg\": { \"value\":"+obj.temperature+", \"type\": \"Float\"}}", // Stringified JSON message
           "http://context-broker-url:8080/v2/entities/Room1", // URL
           ContentType.JSON, // Content type
           HTTPMethod.POST) // HTTP method
    )

   OrionSink.addSink( processedDataStream )

```

The sink accepts a `DataStream` of objects of the class **`OrionSinkObject`**. This class has 5 attributes:

-   **`content`**: Message content in String format. If it is a JSON, you need to make sure to stringify it before
    sending it.

-   **`url`**: URL to which the message should be sent.

-   **`contentType`**: Type of HTTP content of the message. It can be `ContentType.JSON` or `ContentType.Plain`.

-   **`method`**: HTTP method of the message. It can be `HTTPMethod.POST`, `HTTPMethod.PUT` or `HTTPMethod.PATCH`.

-   **`headers`** (Optional): String Map including any additional HTTP headers.

### Production

When packaging your code in a JAR, it is common to exclude dependencies like Spark and Scala since they are typically
provided by the execution environment. Nevertheless, it is necessary to include this connector in your packaged code,
since it is not part of the Spark distribution.
