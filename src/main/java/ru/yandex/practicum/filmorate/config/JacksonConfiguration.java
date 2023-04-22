package ru.yandex.practicum.filmorate.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {

        return builder -> {

            builder.deserializers(new StdDeserializer<Duration>(Duration.class) {
                @Override
                public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                        throws IOException {
                    return Duration.ofMillis(jsonParser.getLongValue());
                }
            });

            builder.serializers(new StdSerializer<>(Duration.class) {
                @Override
                public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider
                        serializerProvider) throws IOException {
                    jsonGenerator.writeNumber(duration.toMillis());
                }
            });
        };
    }
}