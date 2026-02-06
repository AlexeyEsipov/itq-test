package ru.itq.reestr.mapper;

import org.mapstruct.Mapper;
import ru.itq.reestr.dto.DocDtoStatus;
import ru.itq.reestr.model.DocEntity;

@Mapper(componentModel = "spring")
public interface DocStatusMapper extends Mappable<DocEntity, DocDtoStatus>{
}
