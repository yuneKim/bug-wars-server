package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.BugAssemblyParseDTO;
import net.crusadergames.bugwars.parser.BugAssemblyParseException;
import net.crusadergames.bugwars.parser.BugAssemblyParser;
import net.crusadergames.bugwars.parser.BugAssemblyParserFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BugAssemblyParserServiceTests {
    private final BugAssemblyParserFactory bugAssemblyParserFactory = Mockito.mock(BugAssemblyParserFactory.class);
    private BugAssemblyParserService bugAssemblyParserService;

    @BeforeEach
    public void setup() {
        bugAssemblyParserService = new BugAssemblyParserService(bugAssemblyParserFactory);
    }

    @Test
    public void parse_returnsBytecode() throws BugAssemblyParseException {
        BugAssemblyParseDTO bugAssemblyParseDTO = new BugAssemblyParseDTO(":START\ngoto START");
        List<Integer> expectedResult = List.of(35, 0);

        BugAssemblyParser bugAssemblyParser = mock(BugAssemblyParser.class);
        when(bugAssemblyParserFactory.createInstance()).thenReturn(bugAssemblyParser);
        when(bugAssemblyParser.parse(bugAssemblyParseDTO.getCode())).thenReturn(expectedResult);

        List<Integer> byteCode = bugAssemblyParserService.parse(bugAssemblyParseDTO);

        Assertions.assertThat(byteCode).hasSize(2).hasSameElementsAs(expectedResult);
    }

    @Test
    public void parse_throwsBugAssemblyParseExceptionOnParseException() throws BugAssemblyParseException {
        BugAssemblyParseDTO bugAssemblyParseDTO = new BugAssemblyParseDTO("  rotr something\n");

        BugAssemblyParser bugAssemblyParser = mock(BugAssemblyParser.class);
        when(bugAssemblyParserFactory.createInstance()).thenReturn(bugAssemblyParser);
        when(bugAssemblyParser.parse(bugAssemblyParseDTO.getCode())).thenThrow(BugAssemblyParseException.class);

        Assertions.assertThatThrownBy(() -> bugAssemblyParserService.parse(bugAssemblyParseDTO))
                .isInstanceOf(BugAssemblyParseException.class);
    }
}
