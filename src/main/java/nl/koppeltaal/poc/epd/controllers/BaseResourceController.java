package nl.koppeltaal.poc.epd.controllers;

import com.auth0.jwk.JwkException;
import nl.koppeltaal.poc.fhir.dto.BaseDto;
import nl.koppeltaal.poc.fhir.dto.DtoConverter;
import nl.koppeltaal.poc.fhir.exception.EnitityNotFoundException;
import nl.koppeltaal.poc.fhir.service.BaseFhirClientService;
import nl.koppeltaal.poc.utils.UrlUtils;
import org.hl7.fhir.r4.model.DomainResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BaseResourceController<D extends BaseDto, R extends DomainResource> {

	final BaseFhirClientService<D, R> fhirClientService;
	final DtoConverter<D, R> dtoConverter;

	public BaseResourceController(BaseFhirClientService<D, R> fhirClientService, DtoConverter<D, R> dtoConverter) {
		this.fhirClientService = fhirClientService;
		this.dtoConverter = dtoConverter;
	}

	@RequestMapping(value = "{reference}", method = RequestMethod.DELETE)
	public void delete(@PathVariable String reference) throws IOException, JwkException {
		fhirClientService.deleteResourceByReference(reference);
	}

	@RequestMapping(value = "{reference}", method = RequestMethod.GET)
	public D get(@PathVariable String reference) throws IOException, JwkException {
		R activitydefinition = fhirClientService.getResourceByReference(reference);
		if (activitydefinition != null) {
			return dtoConverter.convert(activitydefinition);
		} else {
			throw new EnitityNotFoundException("Cannot locate activitydefinition " + reference);
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<D> list(HttpSession httpSession) throws IOException, JwkException {
		List<D> rv = new ArrayList<>();
		List<R> activitydefinitions = fhirClientService.getResources();
		for (R activitydefinition : activitydefinitions) {
			rv.add(dtoConverter.convert(activitydefinition));
		}
		return rv;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public D put(HttpServletRequest request, @RequestBody D activitydefinitionDto) throws IOException, JwkException {
		return dtoConverter.convert(fhirClientService.storeResource(UrlUtils.getServerUrl("", request), dtoConverter.convert(activitydefinitionDto)));
	}

}
