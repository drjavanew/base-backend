package org.lamisplus.base.domain.dto;

import lombok.extern.slf4j.Slf4j;
import org.lamisplus.base.domain.entities.Person;
import org.lamisplus.base.domain.entities.PersonContact;
import org.lamisplus.base.domain.entities.PersonRelative;
import org.modelmapper.ModelMapper;

@Slf4j
public class DtoToEntity {


    public static Person convertPersonDTOToPerson(PersonDTO personDTO) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(personDTO, Person.class);
    }

/*

    public static PersonResponse convertPersonToPersonDTO1(Person person) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(person, PersonResponse.class);
    }
*/


    public static PersonContact convertPersonContactDTOToPersonContact(PersonContactDTO personContactDTO) {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(personContactDTO, PersonContact.class);
    }


    public static PersonRelative convertPersonRelativeDTORelative(PersonRelativesDTO personRelativesDTO) {
        return new ModelMapper().map(personRelativesDTO, PersonRelative.class);
    }


}
