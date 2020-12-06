package com.raj.nola.producer;

import com.raj.nola.config.SystemConfig;
import com.raj.nola.reader.CustomerFileReader;
import com.raj.nola.types.Customer;
import com.raj.nola.util.JsonSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.LongSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class CustomerProducer {

    public static void main(String[] args) {

        System.out.println("Initializing Producer...");

        Properties props = new Properties();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, SystemConfig.producerApplicationID);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SystemConfig.bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        KafkaProducer<Integer, Customer> producer = new KafkaProducer<Integer, Customer>(props);

        Customer[] customers=CustomerFileReader.getInstance().getCustomers();

        for(int i = 0; i < customers.length; i++) {

            ProducerRecord<Integer, Customer> record=
                    new ProducerRecord<Integer, Customer>(SystemConfig.topicName, i,customers[i]);
            try {

                RecordMetadata recordMetadata = producer.send(record).get();

                String message = String.format("sent message to topic:%s partition:%s  offset:%s",
                        recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
                System.out.println(message);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }


    }
}


