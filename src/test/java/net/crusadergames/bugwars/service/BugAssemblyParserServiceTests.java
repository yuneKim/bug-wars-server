package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.BugAssemblyParseRequest;
import net.crusadergames.bugwars.parser.BugAssemblyParseException;
import net.crusadergames.bugwars.parser.BugAssemblyParser;
import net.crusadergames.bugwars.parser.BugAssemblyParserFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BugAssemblyParserServiceTests {
    @Mock
    private BugAssemblyParserFactory bugAssemblyParserFactory;

    @InjectMocks
    private BugAssemblyParserService bugAssemblyParserService;

    @Test
    public void parse_returnsBytecode() throws BugAssemblyParseException {
        BugAssemblyParseRequest bugAssemblyParseRequest = new BugAssemblyParseRequest(":START\ngoto START");
        List<Integer> expectedResult = List.of(35, 0);

        BugAssemblyParser bugAssemblyParser = mock(BugAssemblyParser.class);
        when(bugAssemblyParserFactory.createInstance()).thenReturn(bugAssemblyParser);
        when(bugAssemblyParser.parse(bugAssemblyParseRequest.getCode())).thenReturn(expectedResult);

        List<Integer> byteCode = bugAssemblyParserService.parse(bugAssemblyParseRequest);

        Assertions.assertThat(byteCode).hasSize(2).hasSameElementsAs(expectedResult);
    }

    @Test
    public void parse_respondsWithUnprocessableEntityOnParseException() throws BugAssemblyParseException {
        BugAssemblyParseRequest bugAssemblyParseRequest = new BugAssemblyParseRequest("  rotr something\n");

        BugAssemblyParser bugAssemblyParser = mock(BugAssemblyParser.class);
        when(bugAssemblyParserFactory.createInstance()).thenReturn(bugAssemblyParser);
        when(bugAssemblyParser.parse(bugAssemblyParseRequest.getCode())).thenThrow(BugAssemblyParseException.class);

        Assertions.assertThatThrownBy(() -> bugAssemblyParserService.parse(bugAssemblyParseRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
