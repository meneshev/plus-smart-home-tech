package delivery.dal.mapper;

import delivery.dal.entity.DeliveryAddress;
import dto.warehouse.AddressDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeliveryAddressMapper {

    public static DeliveryAddress toEntity(AddressDto dto) {
        return DeliveryAddress.builder()
                .country(dto.getCountry())
                .city(dto.getCity())
                .street(dto.getStreet())
                .house(dto.getHouse())
                .flat(dto.getFlat())
                .build();
    }

    public static AddressDto toDto(DeliveryAddress entity) {
        return AddressDto.builder()
                .country(entity.getCountry())
                .city(entity.getCity())
                .street(entity.getStreet())
                .house(entity.getHouse())
                .flat(entity.getFlat())
                .build();
    }
}
