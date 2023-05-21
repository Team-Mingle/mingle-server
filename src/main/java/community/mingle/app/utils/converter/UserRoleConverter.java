package community.mingle.app.utils.converter;

import community.mingle.app.src.domain.UserRole;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;

@Converter
public class UserRoleConverter implements AttributeConverter<UserRole, String> {
    @Override
    public String convertToDatabaseColumn(UserRole attribute) {
        return attribute.toString();
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        return Arrays.stream(UserRole.values())
                .filter(enumValue -> enumValue.toString().equalsIgnoreCase(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No enum matches with the string value: " + dbData));
    }
}
