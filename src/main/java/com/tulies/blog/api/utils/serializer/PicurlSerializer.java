package com.tulies.blog.api.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.tulies.blog.api.config.BaseConfigProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class PicurlSerializer extends JsonSerializer<String> {

    @Autowired
    private BaseConfigProperties baseConfigProperties;

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if (StringUtils.isNotBlank(s) && !s.startsWith("http") && !s.startsWith("//")) {
            String fileUrlHost = baseConfigProperties.getFileUrlHost();
            jsonGenerator.writeString(fileUrlHost + s);
        } else {
            jsonGenerator.writeString(s);
        }
    }
}