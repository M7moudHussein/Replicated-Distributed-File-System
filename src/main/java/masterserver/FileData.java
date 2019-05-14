package masterserver;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class FileData implements Serializable {
    private final String fileName;
    private final String fileContent;
}
