package warehouse.dal.mapper;

import dto.AddressDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import warehouse.dal.entity.Address;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AddressMapper {

    public static AddressDto convertToDto(Address address) {
        return AddressDto.builder()
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat() != null ? address.getFlat() : null)
                .build();
    }
}