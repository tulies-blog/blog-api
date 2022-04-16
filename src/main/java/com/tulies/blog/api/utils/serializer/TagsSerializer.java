package com.tulies.blog.api.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class TagsSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if(StringUtils.isNotBlank(s)) {
            if(s.startsWith(",")) {
                s=s.substring(1);
            }
            if(s.endsWith(",")) {
                s=s.substring(0,s.length()-1);
            }
            jsonGenerator.writeString(s);
        }else{
            jsonGenerator.writeString(s);
        }
    }
}