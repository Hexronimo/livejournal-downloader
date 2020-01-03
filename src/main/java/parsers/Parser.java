package parsers;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public interface Parser {

    List<String> parse(String journal);

    void setPeriod(LocalDate start, LocalDate end);

    void  setQuantity(int q);

}
