package feedzupzup.backend.global.convert;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Converter
public class DoubleArrayConverter implements AttributeConverter<double[], String> {

    @Override
    public String convertToDatabaseColumn(final double[] attribute) {
        if (attribute == null) {
            return null;
        }
        return Arrays.stream(attribute)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public double[] convertToEntityAttribute(String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        return Arrays.stream(data.split(","))
                .mapToDouble(Double::parseDouble)
                .toArray();
    }
}
