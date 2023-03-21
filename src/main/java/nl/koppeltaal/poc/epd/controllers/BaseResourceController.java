package nl.koppeltaal.poc.epd.controllers;

import ca.uhn.fhir.rest.api.SortSpec;
import com.auth0.jwk.JwkException;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.BaseDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.DtoConverter;
import nl.koppeltaal.spring.boot.starter.smartservice.exception.EnitityNotFoundException;
import nl.koppeltaal.spring.boot.starter.smartservice.service.fhir.BaseFhirClientCrudService;
import org.hl7.fhir.r4.model.DomainResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BaseResourceController<D extends BaseDto, R extends DomainResource> {

	final BaseFhirClientCrudService<D, R> fhirClientService;
	final DtoConverter<D, R> dtoConverter;

	/**
	 * Allow subclasses to override the sorting spec
	 * @return
	 */
	public SortSpec getSortSpec() {
		return null;
	}

	public BaseResourceController(BaseFhirClientCrudService<D, R> fhirClientService, DtoConverter<D, R> dtoConverter) {
		this.fhirClientService = fhirClientService;
		this.dtoConverter = dtoConverter;
	}

	@RequestMapping(value = "{reference}", method = RequestMethod.DELETE)
	public void delete(@PathVariable String reference) throws IOException, JwkException {
		fhirClientService.deleteByMarkingAsEndOfLife(reference);
	}

	@RequestMapping(value = "{reference}", method = RequestMethod.GET)
	public D get(@PathVariable String reference) throws IOException, JwkException {
		R resource = fhirClientService.getResourceByReference(reference);
		if (resource != null) {
			return dtoConverter.convert(resource);
		} else {
			throw new EnitityNotFoundException("Cannot locate resource " + reference);
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<D> list() {
		List<D> rv = new ArrayList<>();
		List<R> resources = fhirClientService.getResources(getSortSpec());
		for (R resource : resources) {
			rv.add(dtoConverter.convert(resource));
		}
		return rv;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public D put(HttpServletRequest request, @RequestBody D activitydefinitionDto) throws IOException {
		return dtoConverter.convert(fhirClientService.storeResource(dtoConverter.convert(activitydefinitionDto)));
	}

}
