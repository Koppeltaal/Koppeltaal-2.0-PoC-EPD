package nl.koppeltaal.poc.epd.controllers;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;
import nl.koppeltaal.spring.boot.starter.smartservice.service.fhir.SearchService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("api/search")
public class SearchController {

  private final SearchService service;

  public SearchController(SearchService service) {
    this.service = service;
  }

  @GetMapping
  public String search(@RequestParam String query, HttpServletResponse response) throws IOException {

    final String[] split = query.split("\\?");

    return service.rawSearch(split[0], split.length == 2 ? split[1] : null);
  }

  @ExceptionHandler(HttpClientErrorException.class)
  public SearchError error(HttpClientErrorException e, HttpServletResponse response) {

    response.setStatus(e.getRawStatusCode());

    return new SearchError(e.getResponseBodyAsString(), e.getRawStatusCode());
  }

  static class SearchError {
    private final String message;
    private final int statusCode;

    SearchError(String message, int statusCode) {
      Matcher matcher = Pattern.compile(".*<pre>(.*)</pre>.*").matcher(message);

      this.message = matcher.find() ? matcher.group(1).replaceAll("&quot;", "'") : message;
      this.statusCode = statusCode;
    }

    public String getMessage() {
      return message;
    }

    public int getStatusCode() {
      return statusCode;
    }
  }

}
